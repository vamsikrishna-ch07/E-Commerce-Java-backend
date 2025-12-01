package com.ecommerce.cartservice.service;

import com.ecommerce.cartservice.client.ProductClient;
import com.ecommerce.cartservice.dto.AddToCartRequest;
import com.ecommerce.cartservice.dto.CartItemResponse;
import com.ecommerce.cartservice.dto.CartResponse;
import com.ecommerce.cartservice.dto.ProductResponse;
import com.ecommerce.cartservice.model.Cart;
import com.ecommerce.cartservice.model.CartItem;
import com.ecommerce.cartservice.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductClient productClient;

    @Override
    public CartResponse addToCart(AddToCartRequest request) {

        // 🔥 STEP 1: Get Product Details from Product Service
        ProductResponse product = productClient.getProductById(request.getProductId());
        if (product == null) {
            throw new RuntimeException("Product not found!");
        }

        // 🔥 STEP 2: Validate Stock
        if (product.getQuantity() < request.getQuantity()) {
            throw new RuntimeException("Product is out of stock or insufficient quantity!");
        }

        // 🔥 STEP 3: Get Cart or Create New
        Cart cart = cartRepository.findByUserId(request.getUserId())
                .orElseGet(() -> Cart.builder()
                        .userId(request.getUserId())
                        .items(new ArrayList<>())
                        .build()
                );

        // 🔥 STEP 4: Add Item to Cart
        CartItem item = CartItem.builder()
                .productId(product.getId())
                .productName(product.getName())
                .price(product.getPrice().doubleValue())
                .quantity(request.getQuantity())
                .build();

        cart.getItems().add(item);

        // 🔥 STEP 5: Recalculate Total
        cart.setTotalPrice(
                cart.getItems().stream()
                        .mapToDouble(cartItem -> cartItem.getPrice() * cartItem.getQuantity())
                        .sum()
        );

        Cart saved = cartRepository.save(cart);

        return convertToResponse(saved);
    }

    @Override
    public CartResponse getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        return convertToResponse(cart);
    }

    private CartResponse convertToResponse(Cart cart) {
        return CartResponse.builder()
                .cartId(cart.getId())
                .userId(cart.getUserId())
                .totalPrice(cart.getTotalPrice())
                .items(cart.getItems().stream()
                        .map(i -> CartItemResponse.builder()
                                .productId(i.getProductId())
                                .productName(i.getProductName())
                                .price(i.getPrice())
                                .quantity(i.getQuantity())
                                .totalPrice(i.getPrice() * i.getQuantity())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
