package com.ecommerce.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentVerifyRequest {
    private Long orderId;
    private String transactionId; // Transaction ID received from payment gateway after initiation
    // Add more fields as needed for verification (e.g., payment gateway specific tokens)
}
