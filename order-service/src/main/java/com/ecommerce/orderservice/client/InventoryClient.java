package com.ecommerce.orderservice.client;

import com.ecommerce.orderservice.dto.StockUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventory-service")
public interface InventoryClient {

    @PutMapping("/api/v1/inventory/stock")
    void updateStock(@RequestBody StockUpdateRequest request);
}
