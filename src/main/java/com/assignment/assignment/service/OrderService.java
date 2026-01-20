package com.assignment.assignment.service;

import com.assignment.assignment.dto.OrderResponse;
import com.assignment.assignment.model.*;
import com.assignment.assignment.repository.OrderRepository;
import com.assignment.assignment.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @Autowired
    private PaymentService paymentService;

    public Order placeOrder(String userId) throws Exception {
        // Get cart items
        List<CartItem> cartItems = cartService.getCartItemsRaw(userId);

        if (cartItems.isEmpty()) {
            throw new Exception("Cart is empty");
        }

        // Calculate total and validate stock
        double totalAmount = 0.0;
        for (CartItem cartItem : cartItems) {
            Product product = productService.getProductById(cartItem.getProductId());
            if (product == null) {
                throw new Exception("Product not found: " + cartItem.getProductId());
            }
            if (product.getStock() < cartItem.getQuantity()) {
                throw new Exception("Insufficient stock for product: " + product.getName());
            }
            totalAmount += product.getPrice() * cartItem.getQuantity();
        }

        // Create order
        Order order = new Order(userId, totalAmount, "CREATED");
        order = orderRepository.save(order);

        // Create order items and update stock
        for (CartItem cartItem : cartItems) {
            Product product = productService.getProductById(cartItem.getProductId());
            OrderItem orderItem = new OrderItem(
                    order.getId(),
                    cartItem.getProductId(),
                    cartItem.getQuantity(),
                    product.getPrice());
            orderItemRepository.save(orderItem);

            // Update product stock
            productService.updateStock(cartItem.getProductId(),
                    product.getStock() - cartItem.getQuantity());
        }

        // Clear cart
        cartService.clearCart(userId);

        return order;
    }

    public OrderResponse getOrderById(String orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return null;
        }

        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUserId());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus());
        response.setCreatedAt(order.getCreatedAt());

        // Get payment info
        Payment payment = paymentService.getPaymentByOrderId(orderId);
        if (payment != null) {
            OrderResponse.PaymentInfo paymentInfo = new OrderResponse.PaymentInfo(
                    payment.getId(),
                    payment.getStatus(),
                    payment.getAmount());
            response.setPayment(paymentInfo);
        }

        // Get order items
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        List<OrderResponse.OrderItemInfo> itemInfos = orderItems.stream()
                .map(item -> new OrderResponse.OrderItemInfo(
                        item.getProductId(),
                        item.getQuantity(),
                        item.getPrice()))
                .collect(Collectors.toList());
        response.setItems(itemInfos);

        return response;
    }

    public List<Order> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }

    public Order updateOrderStatus(String orderId, String status) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus(status);
            return orderRepository.save(order);
        }
        return null;
    }

    public Order cancelOrder(String orderId) throws Exception {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            throw new Exception("Order not found");
        }

        if ("PAID".equals(order.getStatus())) {
            throw new Exception("Cannot cancel paid order");
        }

        if ("CANCELLED".equals(order.getStatus())) {
            throw new Exception("Order is already cancelled");
        }

        order.setStatus("CANCELLED");
        order = orderRepository.save(order);

        // Restore stock
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        for (OrderItem item : orderItems) {
            Product product = productService.getProductById(item.getProductId());
            if (product != null) {
                productService.updateStock(item.getProductId(), product.getStock() + item.getQuantity());
            }
        }

        return order;
    }
}
