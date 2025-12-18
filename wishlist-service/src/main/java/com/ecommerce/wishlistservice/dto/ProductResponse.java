package com.ecommerce.wishlistservice.dto;

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
    private Long productId; // Changed from id
    private String name;
    private String description;
    private String brand;
    private String category;
    private BigDecimal price;
    private String imageUrl;
    // Removed 'quantity' as it's not part of product-service's ProductResponse
}
