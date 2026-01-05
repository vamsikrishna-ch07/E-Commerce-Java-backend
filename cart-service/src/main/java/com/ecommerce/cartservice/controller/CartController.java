package com.ecommerce.cartservice.controller;

import com.ecommerce.cartservice.dto.AddToCartRequest;
import com.ecommerce.cartservice.dto.CartResponse;
import com.ecommerce.cartservice.dto.UpdateCartItemRequest;
import com.ecommerce.cartservice.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    private Long getUserId(Principal principal) {
        return Long.valueOf(principal.getName());
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartResponse> getCart(Principal principal) {
        Long userId = getUserId(principal);
        CartResponse cart = cartService.getCart(userId);
        return ResponseEntity.ok(cart);
    }

    // New endpoint for internal service-to-service communication
    @GetMapping("/internal/{userId}")
    @PreAuthorize("hasAuthority('SCOPE_internal.read')")
    public ResponseEntity<CartResponse> getCartByUserId(@PathVariable("userId") Long userId) {
        CartResponse cart = cartService.getCart(userId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartResponse> addToCart(Principal principal, @RequestBody AddToCartRequest request) {
        request.setUserId(getUserId(principal));
        CartResponse cart = cartService.addToCart(request);
        return new ResponseEntity<>(cart, HttpStatus.CREATED);
    }

    @PutMapping("/update/{productId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartResponse> updateCartItem(
            Principal principal,
            @PathVariable("productId") Long productId,
            @RequestBody UpdateCartItemRequest request) {
        Long userId = getUserId(principal);
        CartResponse cart = cartService.updateCartItem(userId, productId, request);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/remove/{productId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> removeCartItem(Principal principal, @PathVariable("productId") Long productId) {
        Long userId = getUserId(principal);
        cartService.removeCartItem(userId, productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clear")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> clearCart(Principal principal) {
        Long userId = getUserId(principal);
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    // New endpoint for internal service-to-service communication
    @DeleteMapping("/internal/{userId}/clear")
    @PreAuthorize("hasAuthority('SCOPE_internal.write')")
    public ResponseEntity<Void> clearCartByUserId(@PathVariable("userId") Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
