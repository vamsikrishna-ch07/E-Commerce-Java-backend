package com.ecommerce.paymentservice.service;

import com.ecommerce.paymentservice.dto.PaymentInitiateRequest;
import com.ecommerce.paymentservice.dto.PaymentRequest; // Import PaymentRequest
import com.ecommerce.paymentservice.dto.PaymentResponse;
import com.ecommerce.paymentservice.dto.PaymentVerifyRequest;

public interface PaymentService {
    PaymentResponse initiatePayment(PaymentInitiateRequest request);
    PaymentResponse verifyPayment(PaymentVerifyRequest request);
    PaymentResponse getPaymentStatusByOrderId(Long orderId);
    PaymentResponse processPayment(PaymentRequest request); // New method for internal service calls
}
