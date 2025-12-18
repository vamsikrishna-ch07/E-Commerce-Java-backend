package com.ecommerce.wishlistservice.controller;

import com.ecommerce.wishlistservice.dto.AddToWishlistRequest;
import com.ecommerce.wishlistservice.dto.WishlistResponse;
import com.ecommerce.wishlistservice.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    private Long getUserId(Principal principal) {
        return Long.valueOf(principal.getName());
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<WishlistResponse> getWishlist(Principal principal) {
        Long userId = getUserId(principal);
        return ResponseEntity.ok(wishlistService.getWishlistByUserId(userId));
    }

    @PostMapping("/{productId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<WishlistResponse> addItemToWishlist(Principal principal, @PathVariable Long productId) {
        Long userId = getUserId(principal);
        AddToWishlistRequest request = AddToWishlistRequest.builder()
                .userId(userId)
                .productId(productId)
                .build();
        return new ResponseEntity<>(wishlistService.addItemToWishlist(request), HttpStatus.CREATED);
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> removeItemFromWishlist(Principal principal, @PathVariable Long productId) {
        Long userId = getUserId(principal);
        wishlistService.removeItemFromWishlist(userId, productId);
        return ResponseEntity.noContent().build();
    }
}
