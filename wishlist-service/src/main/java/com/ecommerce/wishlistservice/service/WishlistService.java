package com.ecommerce.wishlistservice.service;

import com.ecommerce.wishlistservice.dto.AddToWishlistRequest;
import com.ecommerce.wishlistservice.dto.WishlistResponse;

public interface WishlistService {
    WishlistResponse addItemToWishlist(AddToWishlistRequest request);
    WishlistResponse getWishlistByUserId(Long userId);
    void removeItemFromWishlist(Long userId, Long productId);
}
