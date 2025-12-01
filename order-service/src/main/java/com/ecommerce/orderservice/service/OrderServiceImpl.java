package com.ecommerce.orderservice.service;

import com.ecommerce.orderservice.client.*;
import com.ecommerce.orderservice.dto.*;
import com.ecommerce.orderservice.entity.Order;
import com.ecommerce.orderservice.entity.OrderItem;
import com.ecommerce.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartClient cartClient;
    private final UserClient userClient;
    private final PaymentClient paymentClient;
    private final InventoryClient inventoryClient;
    private final NotificationClient notificationClient;

    @Override
    @Transactional
    public Order placeOrder(Long userId) {
        try {
            userClient.getUserById(userId);
        } catch (Exception e) {
            throw new RuntimeException("User not found, cannot place order.");
        }

        CartResponse cart = cartClient.getCartByUserId(userId);
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty or not found!");
        }

        Order order = Order.builder()
                .userId(userId)
                .orderStatus("CREATED")
                .orderDate(LocalDateTime.now())
                .totalAmount(BigDecimal.valueOf(cart.getTotalPrice()))
                .build();

        var items = cart.getItems().stream()
                .map(item -> OrderItem.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .price(BigDecimal.valueOf(item.getPrice()))
                        .order(order)
                        .build())
                .collect(Collectors.toList());
        order.setItems(items);
        
        Order savedOrder = orderRepository.save(order);

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(savedOrder.getId())
                .amount(savedOrder.getTotalAmount())
                .build();
        
        try {
            paymentClient.processPayment(paymentRequest);
            savedOrder.setOrderStatus("PENDING_PAYMENT");
        } catch (Exception e) {
            savedOrder.setOrderStatus("PAYMENT_FAILED");
        }

        return orderRepository.save(savedOrder);
    }

    @Override
    @Transactional
    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Handle different status updates
        if ("SUCCESS".equals(status) && !"SUCCESS".equals(order.getOrderStatus())) {
            handleSuccessfulPayment(order);
        } else if ("SHIPPED".equals(status) && !"SHIPPED".equals(order.getOrderStatus())) {
            handleShipping(order);
        }

        order.setOrderStatus(status);
        orderRepository.save(order);
    }

    private void handleSuccessfulPayment(Order order) {
        // Reduce stock
        for (OrderItem item : order.getItems()) {
            StockUpdateRequest stockUpdateRequest = StockUpdateRequest.builder()
                    .productId(item.getProductId())
                    .quantity(-item.getQuantity())
                    .build();
            inventoryClient.updateStock(stockUpdateRequest);
        }

        // Send order confirmation email
        sendNotification(order, "Order Confirmation #" + order.getId(),
                "<h1>Your Order is Confirmed!</h1><p>Hi %s,</p><p>Your order #%d with a total of $%.2f has been successfully placed.</p>");
    }

    private void handleShipping(Order order) {
        // Send shipping notification email
        sendNotification(order, "Your Order Has Shipped!",
                "<h1>Your Order is on its way!</h1><p>Hi %s,</p><p>Your order #%d has been shipped and will arrive soon.</p>");
    }

    private void sendNotification(Order order, String subject, String bodyTemplate) {
        try {
            UserResponse user = userClient.getUserById(order.getUserId());
            String emailBody = String.format(bodyTemplate, user.getUsername(), order.getId(), order.getTotalAmount());
            NotificationRequest notificationRequest = NotificationRequest.builder()
                    .to(user.getEmail())
                    .subject(subject)
                    .body(emailBody)
                    .build();
            notificationClient.sendNotification(notificationRequest);
        } catch (Exception e) {
            System.err.println("Failed to send notification for order: " + order.getId() + ". Error: " + e.getMessage());
        }
    }
}
