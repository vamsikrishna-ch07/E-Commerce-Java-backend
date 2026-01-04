package com.ecommerce.inventoryservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Only include non-null fields in JSON
public class InventoryResponse {
    private Long productId;
    private Integer quantity;
    private Boolean inStock; // New field for users
}
