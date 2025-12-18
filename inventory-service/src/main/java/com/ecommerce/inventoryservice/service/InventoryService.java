package com.ecommerce.inventoryservice.service;

import com.ecommerce.inventoryservice.dto.InventoryResponse;
import com.ecommerce.inventoryservice.dto.StockUpdateRequest;

public interface InventoryService {
    InventoryResponse getInventoryByProductId(Long productId);
    void createInventory(StockUpdateRequest request);
    void updateStock(StockUpdateRequest request);
    void reduceStock(Long productId, Integer quantity); // New method
    void restoreStock(Long productId, Integer quantity); // New method
}
