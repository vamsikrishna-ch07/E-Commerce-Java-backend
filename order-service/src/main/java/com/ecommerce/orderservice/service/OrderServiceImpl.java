package com.ecommerce.orderservice.service;

import com.ecommerce.orderservice.client.*;
import com.ecommerce.orderservice.dto.*;
import com.ecommerce.orderservice.model.Order;
import com.ecommerce.orderservice.model.OrderItem;
import com.ecommerce.orderservice.model.OrderStatus;
import com.ecommerce.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
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

    // Helper to map Order entity to OrderResponse DTO
    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUserId())
                .totalPrice(order.getTotalPrice())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .shippingAddress(order.getShippingAddress())
                .orderItems(itemResponses)
                .build();
    }

    @Override
    @Transactional
    public OrderResponse checkout(Long userId, OrderRequest orderRequest) {
        // 1. Validate User
        UserResponse user = userClient.getUserById(userId);

        // 2. Get Cart Details
        CartResponse cart = cartClient.getCartByUserId(userId);
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty or not found for user: " + userId);
        }

        // 3. Reduce stock for all items
        for (CartItemResponse cartItem : cart.getItems()) {
            inventoryClient.reduceStock(cartItem.getProductId(), cartItem.getQuantity());
        }

        // 4. Create Order with PENDING status. Payment is NOT processed here.
        Order order = Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .userId(userId)
                .totalPrice(BigDecimal.valueOf(cart.getTotalPrice()))
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING) // The order is pending until payment is confirmed
                .shippingAddress(orderRequest.getShippingAddress())
                .orderItems(cart.getItems().stream()
                        .map(cartItem -> OrderItem.builder()
                                .productId(cartItem.getProductId())
                                .productName(cartItem.getProductName())
                                .quantity(cartItem.getQuantity())
                                .price(BigDecimal.valueOf(cartItem.getPrice()))
                                .build())
                        .collect(Collectors.toList()))
                .build();

        Order savedOrder = orderRepository.save(order);

        // 5. Clear the user's cart
        cartClient.clearCart(userId);

        // 6. Send Order Confirmation Notification
        sendOrderNotification(savedOrder, user.getEmail(), "Order Received",
                String.format("<h1>Your Order #%s has been received.</h1><p>Thank you for your purchase, %s. Please proceed with the payment.</p><p>Total: $%.2f</p>",
                        savedOrder.getOrderNumber(), user.getUsername(), savedOrder.getTotalPrice()));

        return mapToOrderResponse(savedOrder);
    }

    @Override
    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        // Here you could add logic to check for valid status transitions
        order.setStatus(newStatus);
        orderRepository.save(order);

        // Optionally, send a notification about the status update
        if (newStatus == OrderStatus.PROCESSING) {
            UserResponse user = userClient.getUserById(order.getUserId());
            sendOrderNotification(order, user.getEmail(), "Payment Confirmed",
                    String.format("<h1>Payment for Order #%s is Confirmed!</h1><p>Thank you, %s. Your order is now being processed.</p>",
                            order.getOrderNumber(), user.getUsername()));
        }
    }

    @Override
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        return mapToOrderResponse(order);
    }

    @Override
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        // Validate User
        userClient.getUserById(userId); // Throws exception if user not found

        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Order does not belong to user: " + userId);
        }

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.RETURNED) {
            throw new RuntimeException("Order cannot be cancelled in status: " + order.getStatus());
        }

        // Restore stock for all items
        for (OrderItem orderItem : order.getOrderItems()) {
            inventoryClient.restoreStock(orderItem.getProductId(), orderItem.getQuantity());
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        // Send cancellation notification
        UserResponse user = userClient.getUserById(userId); // Fetch user details for notification
        sendOrderNotification(order, user.getEmail(), "Order Cancellation",
                String.format("<h1>Your Order #%s has been Cancelled.</h1><p>We're sorry to see you go, %s.</p>",
                        order.getOrderNumber(), user.getUsername()));
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status).stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    private void sendOrderNotification(Order order, String recipientEmail, String subject, String body) {
        try {
            NotificationRequest notificationRequest = NotificationRequest.builder()
                    .to(recipientEmail)
                    .subject(subject)
                    .body(body)
                    .build();
            notificationClient.sendNotification(notificationRequest);
        } catch (Exception e) {
            System.err.println("Failed to send notification for order: " + order.getOrderNumber() + ". Error: " + e.getMessage());
        }
    }
}
