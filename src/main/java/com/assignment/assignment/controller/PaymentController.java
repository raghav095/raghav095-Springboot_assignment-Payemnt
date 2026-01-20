package com.assignment.assignment.controller;

import com.assignment.assignment.dto.PaymentRequest;
import com.assignment.assignment.model.Payment;
import com.assignment.assignment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createPayment(@Valid @RequestBody PaymentRequest request) {
        try {
            Payment payment = paymentService.initiatePayment(request.getOrderId(), request.getAmount());

            Map<String, Object> response = new HashMap<>();
            response.put("paymentId", payment.getId());
            response.put("orderId", payment.getOrderId());
            response.put("amount", payment.getAmount());
            response.put("status", payment.getStatus());
            response.put("razorpayOrderId", payment.getPaymentId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/key")
    public ResponseEntity<Map<String, String>> getKey() {
        Map<String, String> response = new HashMap<>();
        response.put("key", paymentService.getRazorpayKeyId());
        return ResponseEntity.ok(response);
    }
}
