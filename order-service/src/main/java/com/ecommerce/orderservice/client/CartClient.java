package com.ecommerce.orderservice.client;

import com.ecommerce.common.config.InternalFeignClientConfig;
import com.ecommerce.orderservice.dto.CartResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cart-service", configuration = InternalFeignClientConfig.class)
public interface CartClient {

    @GetMapping("/api/v1/cart/internal/{userId}")
    CartResponse getCartByUserId(@PathVariable("userId") Long userId);

    @DeleteMapping("/api/v1/cart/internal/{userId}/clear")
    void clearCart(@PathVariable("userId") Long userId);
}
