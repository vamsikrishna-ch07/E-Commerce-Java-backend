package com.ecommerce.paymentservice.model;

public enum OrderStatus {
    PENDING,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    RETURNED,
    PAYMENT_FAILED
}
