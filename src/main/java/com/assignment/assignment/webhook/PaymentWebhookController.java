package com.assignment.assignment.webhook;

import com.assignment.assignment.dto.PaymentWebhookRequest;
import com.assignment.assignment.model.Payment;
import com.assignment.assignment.service.PaymentService;
import com.assignment.assignment.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
@CrossOrigin(origins = "*")
public class PaymentWebhookController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OrderService orderService;

    @Value("${razorpay.key-secret}")
    private String razorpayKeySecret;

    @PostMapping("/payment")
    public ResponseEntity<String> handlePaymentWebhook(@RequestBody PaymentWebhookRequest webhookRequest) {
        try {
            System.out.println("Received webhook: " + webhookRequest.getEvent());

            if ("payment.captured".equals(webhookRequest.getEvent())) {
                String razorpayOrderId = webhookRequest.getPayload().getPayment().getOrder_id();
                String paymentStatus = webhookRequest.getPayload().getPayment().getStatus();

                System.out.println("Processing webhook - Razorpay Order ID: " + razorpayOrderId + ", Status: " + paymentStatus);

                // Find payment by Razorpay order ID
                Payment payment = paymentService.getPaymentByRazorpayOrderId(razorpayOrderId);

                if (payment == null) {
                    System.err.println("Payment not found for Razorpay Order ID: " + razorpayOrderId);
                    return ResponseEntity.badRequest().body("Payment not found");
                }

                String internalOrderId = payment.getOrderId();

                if ("captured".equals(paymentStatus)) {
                    paymentService.updatePaymentStatus(internalOrderId, "SUCCESS");
                    orderService.updateOrderStatus(internalOrderId, "PAID");
                    System.out.println("Payment successful for order: " + internalOrderId);
                } else {
                    paymentService.updatePaymentStatus(internalOrderId, "FAILED");
                    orderService.updateOrderStatus(internalOrderId, "FAILED");
                    System.out.println("Payment failed for order: " + internalOrderId);
                }
            }

            return ResponseEntity.ok("Webhook processed successfully");

        } catch (Exception e) {
            System.err.println("Error processing webhook: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error processing webhook");
        }
    }
}
