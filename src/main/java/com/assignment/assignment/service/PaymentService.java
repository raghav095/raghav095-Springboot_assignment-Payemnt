package com.assignment.assignment.service;

import com.assignment.assignment.model.Order;
import com.assignment.assignment.model.Payment;
import com.assignment.assignment.repository.PaymentRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private com.assignment.assignment.repository.OrderRepository orderRepository;

    @Value("${razorpay.key-id}")
    private String razorpayKeyId;

    @Value("${razorpay.key-secret}")
    private String razorpayKeySecret;

    public Payment initiatePayment(String orderId, Double amount) throws Exception {
        // Validate order exists and is in CREATED status
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            throw new Exception("Order not found");
        }
        if (!"CREATED".equals(order.getStatus())) {
            throw new Exception("Order is not in CREATED status");
        }

        try {
            // Create Razorpay order
            RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", (int) (amount * 100)); // Amount in paisa
            orderRequest.put("currency", "INR");

            com.razorpay.Order razorpayOrder = razorpay.orders.create(orderRequest);

            // Create payment record
            Payment payment = new Payment(orderId, amount, "PENDING", razorpayOrder.get("id"));
            return paymentRepository.save(payment);

        } catch (RazorpayException e) {
            throw new Exception("Failed to create Razorpay order: " + e.getMessage());
        }
    }

    public Payment getPaymentByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    public Payment getPaymentByRazorpayOrderId(String razorpayOrderId) {
        // We need to search through all payments to find one with matching paymentId
        // (Razorpay order ID)
        // In a real application, you'd add a repository method for this
        Iterable<Payment> allPayments = paymentRepository.findAll();
        for (Payment payment : allPayments) {
            if (razorpayOrderId.equals(payment.getPaymentId())) {
                return payment;
            }
        }
        return null;
    }

    public void updatePaymentStatus(String orderId, String status) {
        Payment payment = paymentRepository.findByOrderId(orderId);
        if (payment != null) {
            payment.setStatus(status);
            paymentRepository.save(payment);
        }
    }

    public String getRazorpayKeyId() {
        return razorpayKeyId;
    }
}
