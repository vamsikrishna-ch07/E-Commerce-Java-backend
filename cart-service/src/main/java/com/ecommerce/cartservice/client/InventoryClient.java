package com.ecommerce.cartservice.client;

import com.ecommerce.cartservice.dto.InventoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "inventory-service")
public interface InventoryClient {

    @GetMapping("/api/v1/inventory/{productId}")
    InventoryResponse getInventoryByProductId(@PathVariable("productId") Long productId);
}
