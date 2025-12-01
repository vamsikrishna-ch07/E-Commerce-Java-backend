package com.ecommerce.orderservice.service;

import com.ecommerce.orderservice.entity.Order;

public interface OrderService {
    Order placeOrder(Long userId);
    void updateOrderStatus(Long orderId, String status);
}
