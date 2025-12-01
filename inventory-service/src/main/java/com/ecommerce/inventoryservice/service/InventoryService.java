package com.ecommerce.inventoryservice.service;

import com.ecommerce.inventoryservice.dto.InventoryResponse;
import com.ecommerce.inventoryservice.dto.StockUpdateRequest;

public interface InventoryService {
    InventoryResponse getInventoryByProductId(Long productId);
    void updateStock(StockUpdateRequest request);
}
