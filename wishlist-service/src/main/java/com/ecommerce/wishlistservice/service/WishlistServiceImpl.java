package com.ecommerce.wishlistservice.service;

import com.ecommerce.wishlistservice.client.ProductClient;
import com.ecommerce.wishlistservice.dto.AddToWishlistRequest;
import com.ecommerce.wishlistservice.dto.ProductResponse;
import com.ecommerce.wishlistservice.dto.WishlistItemResponse;
import com.ecommerce.wishlistservice.dto.WishlistResponse;
import com.ecommerce.wishlistservice.entity.Wishlist;
import com.ecommerce.wishlistservice.entity.WishlistItem;
import com.ecommerce.wishlistservice.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductClient productClient;

    @Override
    @Transactional
    public WishlistResponse addItemToWishlist(AddToWishlistRequest request) {
        Wishlist wishlist = wishlistRepository.findByUserId(request.getUserId())
                .orElseGet(() -> Wishlist.builder()
                        .userId(request.getUserId())
                        .items(new ArrayList<>())
                        .build());

        boolean itemExists = wishlist.getItems().stream()
                .anyMatch(item -> item.getProductId().equals(request.getProductId()));

        if (!itemExists) {
            WishlistItem newItem = WishlistItem.builder()
                    .productId(request.getProductId())
                    .build();
            wishlist.getItems().add(newItem);
            wishlist = wishlistRepository.save(wishlist);
        }

        return mapToWishlistResponse(wishlist);
    }

    @Override
    public WishlistResponse getWishlistByUserId(Long userId) {
        return wishlistRepository.findByUserId(userId)
                .map(this::mapToWishlistResponse)
                .orElseGet(() -> WishlistResponse.builder() // Return an empty wishlist if not found
                        .userId(userId)
                        .items(new ArrayList<>())
                        .build());
    }

    @Override
    @Transactional
    public void removeItemFromWishlist(Long userId, Long productId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wishlist not found for user: " + userId));

        boolean removed = wishlist.getItems().removeIf(item -> item.getProductId().equals(productId));
        if (!removed) {
            throw new RuntimeException("Product with ID " + productId + " not found in wishlist.");
        }
        wishlistRepository.save(wishlist);
    }

    /**
     * Maps a Wishlist entity to a WishlistResponse DTO.
     * This includes converting each WishlistItem to a WishlistItemResponse.
     */
    private WishlistResponse mapToWishlistResponse(Wishlist wishlist) {
        List<WishlistItemResponse> itemResponses = wishlist.getItems().stream()
                .map(this::mapToWishlistItemResponse)
                .collect(Collectors.toList());

        return WishlistResponse.builder()
                .id(wishlist.getId()) // Corrected field name
                .userId(wishlist.getUserId())
                .items(itemResponses)
                .build();
    }

    /**
     * Maps a WishlistItem entity to a WishlistItemResponse DTO.
     * This involves fetching product details from the product service.
     */
    private WishlistItemResponse mapToWishlistItemResponse(WishlistItem item) {
        try {
            ProductResponse product = productClient.getProductById(item.getProductId());
            return WishlistItemResponse.builder()
                    .productId(product.getProductId()) // Use productId
                    .productName(product.getName())
                    .brand(product.getBrand())
                    .category(product.getCategory())
                    .price(product.getPrice())
                    .imageUrl(product.getImageUrl())
                    .build();
        } catch (Exception e) {
            // Handle case where product service is down or product not found
            return WishlistItemResponse.builder()
                    .productId(item.getProductId())
                    .productName("Product not found or service unavailable")
                    .build();
        }
    }
}
