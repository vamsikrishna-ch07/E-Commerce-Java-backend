package com.ecommerce.paymentservice.service;

import com.ecommerce.paymentservice.dto.PaymentInitiateRequest;
import com.ecommerce.paymentservice.dto.PaymentResponse;
import com.ecommerce.paymentservice.dto.PaymentVerifyRequest;

public interface PaymentService {
    PaymentResponse initiatePayment(PaymentInitiateRequest request);
    PaymentResponse verifyPayment(PaymentVerifyRequest request);
    PaymentResponse getPaymentStatusByOrderId(Long orderId);
}
