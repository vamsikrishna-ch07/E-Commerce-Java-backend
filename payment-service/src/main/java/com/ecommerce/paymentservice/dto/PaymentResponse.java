package com.ecommerce.paymentservice.dto;

import com.ecommerce.paymentservice.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private String transactionId;
    private BigDecimal amount;
    private String paymentMethod;
    private LocalDateTime paymentDate;
    private PaymentStatus status;
    private String failureReason;
}
