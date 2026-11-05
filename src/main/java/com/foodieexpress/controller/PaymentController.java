package com.foodieexpress.controller;

import com.foodieexpress.dto.PaymentVerificationRequestDTO;
import com.foodieexpress.dto.PaymentVerificationResponseDTO;
import com.foodieexpress.dto.RazorpayOrderResponseDTO;
import com.foodieexpress.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // 1. Create a Razorpay Order before opening the checkout payment gateway
    @PostMapping("/create-razorpay-order")
    public ResponseEntity<RazorpayOrderResponseDTO> createRazorpayOrder(@RequestParam String orderNumber) {
        return ResponseEntity.ok(paymentService.createRazorpayOrder(orderNumber));
    }

    // 2. Cryptographically verify signature after user completes payment in Razorpay modal
    @PostMapping("/verify")
    public ResponseEntity<PaymentVerificationResponseDTO> verifyPayment(@RequestBody PaymentVerificationRequestDTO request) {
        return ResponseEntity.ok(paymentService.verifyPayment(request));
    }

    // 3. Handle payment dismiss/failure
    @PostMapping("/failure")
    public ResponseEntity<PaymentVerificationResponseDTO> handlePaymentFailure(
            @RequestParam String orderNumber,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(paymentService.handlePaymentFailure(orderNumber, reason));
    }
}

