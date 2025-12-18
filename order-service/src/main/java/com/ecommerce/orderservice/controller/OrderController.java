package com.ecommerce.orderservice.controller;

import com.ecommerce.orderservice.dto.OrderRequest;
import com.ecommerce.orderservice.dto.OrderResponse;
import com.ecommerce.orderservice.model.OrderStatus;
import com.ecommerce.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    private Long getUserId(Principal principal) {
        // In our setup, the username from the JWT's 'sub' claim is the user ID.
        return Long.valueOf(principal.getName());
    }

    @PostMapping("/checkout")
    @PreAuthorize("hasRole('USER')") // Changed from hasAuthority('SCOPE_orders.write')
    public ResponseEntity<OrderResponse> checkout(Principal principal, @RequestBody OrderRequest orderRequest) {
        Long userId = getUserId(principal);
        OrderResponse newOrder = orderService.checkout(userId, orderRequest);
        return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('USER')") // Changed from hasAuthority('SCOPE_orders.read')
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId, Principal principal) {
        // In a real app, you'd add logic in the service layer to verify
        // that the user (principal.getName()) owns this order, or is an admin.
        OrderResponse order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')") // Changed from hasAuthority('SCOPE_orders.read')
    public ResponseEntity<List<OrderResponse>> getOrdersForCurrentUser(Principal principal) {
        Long userId = getUserId(principal);
        List<OrderResponse> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('USER')") // Changed from hasAuthority('SCOPE_orders.write')
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId, Principal principal) {
        Long userId = getUserId(principal);
        // Service layer should verify that this user owns the order.
        orderService.cancelOrder(userId, orderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(@PathVariable OrderStatus status) {
        List<OrderResponse> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }
}
