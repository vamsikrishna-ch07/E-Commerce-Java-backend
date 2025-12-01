package com.ecommerce.inventoryservice.dto;

import lombok.Data;

@Data
public class InventoryRequest {
    private Long productId;
    private String skuCode;
    private Integer quantity;
}
