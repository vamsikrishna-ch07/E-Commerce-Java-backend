package com.ecommerce.paymentservice.client;

import com.ecommerce.paymentservice.dto.OrderStatusUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping; // Changed from PatchMapping
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "order-service")
public interface OrderClient {

    @PutMapping("/api/v1/orders/{orderId}/status") // Changed from @PatchMapping to @PutMapping
    void updateOrderStatus(@PathVariable("orderId") Long orderId, @RequestBody OrderStatusUpdateRequest request);
}
