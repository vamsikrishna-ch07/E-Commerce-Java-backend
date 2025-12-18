package com.ecommerce.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {
    private Long userId; // Will be set by controller from authenticated user
    private String shippingAddress;
    // Potentially a list of items if not checking out from cart, but for now, assume from cart
}
