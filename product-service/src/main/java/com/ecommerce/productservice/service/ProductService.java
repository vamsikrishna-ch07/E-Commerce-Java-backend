package com.ecommerce.productservice.service;

import com.ecommerce.productservice.dto.ProductRequest;
import com.ecommerce.productservice.dto.ProductResponse;
import com.ecommerce.productservice.dto.ProductDetailsResponse; // New DTO for details endpoint
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    ProductResponse createProduct(ProductRequest productRequest);
    ProductResponse getProductById(Long productId); // Changed to productId
    Page<ProductResponse> getAllProducts(Pageable pageable, String category, String brand); // Added pagination, filtering
    ProductResponse updateProduct(Long productId, ProductRequest productRequest); // Changed to productId
    void deleteProduct(Long productId); // Changed to productId
    ProductDetailsResponse getProductDetails(Long productId); // New method for /details endpoint
}
