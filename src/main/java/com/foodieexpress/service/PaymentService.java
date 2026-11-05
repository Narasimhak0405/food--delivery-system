package com.foodieexpress.service;

import com.foodieexpress.dto.PaymentVerificationRequestDTO;
import com.foodieexpress.dto.PaymentVerificationResponseDTO;
import com.foodieexpress.dto.RazorpayOrderResponseDTO;

public interface PaymentService {
    RazorpayOrderResponseDTO createRazorpayOrder(String orderNumber);
    PaymentVerificationResponseDTO verifyPayment(PaymentVerificationRequestDTO request);
    PaymentVerificationResponseDTO handlePaymentFailure(String orderNumber, String reason);
}

