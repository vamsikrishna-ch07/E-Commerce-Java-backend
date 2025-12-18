package com.ecommerce.orderservice.client;

import com.ecommerce.orderservice.dto.CartResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cart-service")
public interface CartClient {

    @GetMapping("/api/v1/cart") // Corrected: No userId needed in path
    CartResponse getCart();

    @DeleteMapping("/api/v1/cart/clear") // Corrected: No userId needed in path
    void clearCart();
}
