package com.ecommerce.cartservice.service;

import com.ecommerce.cartservice.dto.AddToCartRequest;
import com.ecommerce.cartservice.dto.CartResponse;
import com.ecommerce.cartservice.dto.UpdateCartItemRequest; // New DTO for update

public interface CartService {
    CartResponse addToCart(AddToCartRequest request);
    CartResponse getCart(Long userId); // Keep this for internal use or specific admin access
    CartResponse updateCartItem(Long userId, Long itemId, UpdateCartItemRequest request); // Update quantity of an item
    void removeCartItem(Long userId, Long itemId); // Remove a specific item
    void clearCart(Long userId); // Clear all items from cart
}
