package com.ecommerce.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private Long productId; // Renamed from 'id'
    private String name;
    private String description;
    private String brand;    // Added
    private String category; // Added
    private BigDecimal price;
    private String imageUrl; // Added
    // Removed 'quantity' as it will be fetched separately for product details
}
