package com.ecommerce.wishlistservice.controller;

import com.ecommerce.wishlistservice.dto.AddToWishlistRequest;
import com.ecommerce.wishlistservice.dto.WishlistResponse;
import com.ecommerce.wishlistservice.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping("/add")
    public ResponseEntity<WishlistResponse> addItemToWishlist(@RequestBody AddToWishlistRequest request) {
        return new ResponseEntity<>(wishlistService.addItemToWishlist(request), HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<WishlistResponse> getWishlistByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(wishlistService.getWishlistByUserId(userId));
    }

    @DeleteMapping("/{userId}/items/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItemFromWishlist(@PathVariable Long userId, @PathVariable Long productId) {
        wishlistService.removeItemFromWishlist(userId, productId);
    }
}
