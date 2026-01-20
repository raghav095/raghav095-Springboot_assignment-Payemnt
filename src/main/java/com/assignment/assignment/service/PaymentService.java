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

    @Value("${payment.mode:razorpay}")
    private String paymentMode;

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
            String paymentId;
            String status = "PENDING";

            if ("mock".equalsIgnoreCase(paymentMode)) {
                // Mock Implementation
                paymentId = "pay_mock_" + System.currentTimeMillis();
                System.out.println("Processing Mock Payment for Order: " + orderId);

                // Simulate async processing (in a real app, this might be a separate thread or
                // service)
                // For this assignment, we rely on the Mock Service Flow which says:
                // "Mock service will automatically call webhook after 3 seconds."
                // Since this is the backend itself acting as the "client" to the gateway,
                // and we don't have a separate running mock server on 8081 as per diagram
                // (unless we wrote one),
                // we will simulate the behavior here or assume the user runs one.
                // However, to make this standalone and working "out of the box",
                // we can spawn a thread to call our own webhook after 3 seconds.

                new Thread(() -> {
                    try {
                        Thread.sleep(3000);
                        // Auto-trigger webhook logic internally or via call
                        updatePaymentStatus(orderId, "SUCCESS");
                        order.setStatus("PAID");
                        orderRepository.save(order);
                        System.out.println("Mock Payment Completed for Order: " + orderId);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();

            } else {
                // Razorpay Implementation
                RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
                JSONObject orderRequest = new JSONObject();
                orderRequest.put("amount", (int) (amount * 100)); // Amount in paisa
                orderRequest.put("currency", "INR");

                com.razorpay.Order razorpayOrder = razorpay.orders.create(orderRequest);
                paymentId = razorpayOrder.get("id");
            }

            // Create payment record
            Payment payment = new Payment(orderId, amount, "PENDING", paymentId);
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
