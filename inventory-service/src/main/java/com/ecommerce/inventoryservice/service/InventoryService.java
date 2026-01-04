package com.ecommerce.inventoryservice.service;

import com.ecommerce.inventoryservice.dto.InventoryResponse;
import com.ecommerce.inventoryservice.dto.StockUpdateRequest;

public interface InventoryService {
    InventoryResponse getInventoryByProductId(Long productId, boolean isAdmin); // Updated signature
    void createInventory(StockUpdateRequest request);
    void updateStock(StockUpdateRequest request);
    void reduceStock(Long productId, Integer quantity);
    void restoreStock(Long productId, Integer quantity);
}
