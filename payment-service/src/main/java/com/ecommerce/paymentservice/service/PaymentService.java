package com.ecommerce.paymentservice.service;

import com.ecommerce.paymentservice.dto.PaymentRequest;
import com.ecommerce.paymentservice.entity.Payment;

public interface PaymentService {
    Payment makePayment(PaymentRequest request);
}
