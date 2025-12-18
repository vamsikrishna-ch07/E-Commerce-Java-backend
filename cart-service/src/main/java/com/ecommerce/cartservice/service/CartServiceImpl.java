package com.ecommerce.cartservice.service;

import com.ecommerce.cartservice.client.InventoryClient;
import com.ecommerce.cartservice.client.ProductClient;
import com.ecommerce.cartservice.dto.AddToCartRequest;
import com.ecommerce.cartservice.dto.CartItemResponse;
import com.ecommerce.cartservice.dto.CartResponse;
import com.ecommerce.cartservice.dto.InventoryResponse;
import com.ecommerce.cartservice.dto.ProductResponse;
import com.ecommerce.cartservice.dto.UpdateCartItemRequest;
import com.ecommerce.cartservice.model.Cart;
import com.ecommerce.cartservice.model.CartItem;
import com.ecommerce.cartservice.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductClient productClient;
    private final InventoryClient inventoryClient; // Inject InventoryClient

    @Override
    @Transactional
    public CartResponse addToCart(AddToCartRequest request) {
        // STEP 1: Get Product Details from Product Service
        ProductResponse product = productClient.getProductById(request.getProductId());
        if (product == null) {
            throw new RuntimeException("Product not found!");
        }

        // STEP 2: Validate Stock from Inventory Service
        InventoryResponse inventory = inventoryClient.getInventoryByProductId(request.getProductId());
        if (inventory == null || inventory.getQuantity() < request.getQuantity()) {
            throw new RuntimeException("Product is out of stock or insufficient quantity!");
        }

        // STEP 3: Get Cart or Create New
        Cart cart = cartRepository.findByUserId(request.getUserId())
                .orElseGet(() -> Cart.builder()
                        .userId(request.getUserId())
                        .items(new ArrayList<>())
                        .build()
                );

        // STEP 4: Check if item already exists in cart and update quantity
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + request.getQuantity();
            // Re-validate stock for the new total quantity
            if (inventory.getQuantity() < newQuantity) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName() + ". Available: " + inventory.getQuantity());
            }
            item.setQuantity(newQuantity);
        } else {
            // Add new item to cart
            CartItem item = CartItem.builder()
                    .productId(product.getProductId()) // Use productId from ProductResponse
                    .productName(product.getName())
                    .price(product.getPrice().doubleValue())
                    .quantity(request.getQuantity())
                    .build();
            cart.getItems().add(item);
        }

        // STEP 5: Recalculate Total
        cart.setTotalPrice(
                cart.getItems().stream()
                        .mapToDouble(cartItem -> cartItem.getPrice() * cartItem.getQuantity())
                        .sum()
        );

        Cart saved = cartRepository.save(cart);
        return convertToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> Cart.builder() // Return an empty cart if not found
                        .userId(userId)
                        .items(new ArrayList<>())
                        .totalPrice(0.0)
                        .build());
        return convertToResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse updateCartItem(Long userId, Long productId, UpdateCartItemRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));

        Optional<CartItem> itemToUpdate = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (itemToUpdate.isEmpty()) {
            throw new RuntimeException("Product with ID " + productId + " not found in cart.");
        }

        CartItem item = itemToUpdate.get();
        int requestedQuantity = request.getQuantity();

        if (requestedQuantity <= 0) {
            // If quantity is 0 or less, remove the item
            cart.getItems().remove(item);
        } else {
            // Validate stock for the new quantity
            InventoryResponse inventory = inventoryClient.getInventoryByProductId(productId);
            if (inventory == null || inventory.getQuantity() < requestedQuantity) {
                throw new RuntimeException("Insufficient stock for product: " + item.getProductName() + ". Available: " + (inventory != null ? inventory.getQuantity() : 0));
            }
            item.setQuantity(requestedQuantity);
        }

        // Recalculate Total
        cart.setTotalPrice(
                cart.getItems().stream()
                        .mapToDouble(cartItem -> cartItem.getPrice() * cartItem.getQuantity())
                        .sum()
        );

        Cart saved = cartRepository.save(cart);
        return convertToResponse(saved);
    }

    @Override
    @Transactional
    public void removeCartItem(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));

        boolean removed = cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        if (!removed) {
            throw new RuntimeException("Product with ID " + productId + " not found in cart.");
        }

        // Recalculate Total
        cart.setTotalPrice(
                cart.getItems().stream()
                        .mapToDouble(cartItem -> cartItem.getPrice() * cartItem.getQuantity())
                        .sum()
        );
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));
        cart.getItems().clear();
        cart.setTotalPrice(0.0);
        cartRepository.save(cart);
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
