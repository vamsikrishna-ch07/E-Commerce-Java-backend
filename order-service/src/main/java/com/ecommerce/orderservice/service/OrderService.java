package com.ecommerce.orderservice.service;

import com.ecommerce.orderservice.dto.OrderRequest;
import com.ecommerce.orderservice.dto.OrderResponse;
import com.ecommerce.orderservice.model.OrderStatus;

import java.util.List;

public interface OrderService {
    OrderResponse checkout(Long userId, OrderRequest orderRequest);
    OrderResponse getOrderById(Long orderId);
    List<OrderResponse> getOrdersByUserId(Long userId);
    void cancelOrder(Long userId, Long orderId);
    List<OrderResponse> getAllOrders();
    List<OrderResponse> getOrdersByStatus(OrderStatus status);
}
