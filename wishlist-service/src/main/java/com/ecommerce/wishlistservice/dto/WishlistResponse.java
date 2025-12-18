package com.ecommerce.wishlistservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistResponse {
    private Long id; // Changed from wishlistId to id for consistency with entity
    private Long userId;
    private List<WishlistItemResponse> items;
}
