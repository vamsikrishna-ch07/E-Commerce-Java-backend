package com.ecommerce.orderservice.client;

import com.ecommerce.common.config.InternalFeignClientConfig;
import com.ecommerce.orderservice.dto.InventoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "inventory-service", configuration = InternalFeignClientConfig.class)
public interface InventoryClient {

    @GetMapping("/api/v1/inventory/{productId}")
    InventoryResponse getInventoryByProductId(@PathVariable("productId") Long productId);

    @PutMapping("/api/v1/inventory/{productId}/reduce")
    void reduceStock(@PathVariable("productId") Long productId, @RequestParam("quantity") Integer quantity);

    @PutMapping("/api/v1/inventory/{productId}/restore")
    void restoreStock(@PathVariable("productId") Long productId, @RequestParam("quantity") Integer quantity);
}
