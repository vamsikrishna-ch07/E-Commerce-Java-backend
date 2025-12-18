package com.ecommerce.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentInitiateRequest {
    private Long orderId;
    private Long userId; // User initiating the payment
    private BigDecimal amount;
    private String paymentMethod; // e.g., "CREDIT_CARD", "PAYPAL"
    // Add more fields as needed for a real payment gateway (e.g., card details, return URLs)
}
