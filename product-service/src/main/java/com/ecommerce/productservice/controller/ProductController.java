package com.ecommerce.productservice.controller;

import com.ecommerce.productservice.dto.ProductDetailsResponse;
import com.ecommerce.productservice.dto.ProductRequest;
import com.ecommerce.productservice.dto.ProductResponse;
import com.ecommerce.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse createProduct(@RequestBody ProductRequest productRequest) {
        return productService.createProduct(productRequest);
    }

    @GetMapping("/{productId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable("productId") Long productId) {
        ProductResponse product = productService.getProductById(productId);
        return ResponseEntity.ok(product);
    }

    // New endpoint for internal service-to-service communication
    @GetMapping("/internal/{productId}")
    @PreAuthorize("hasAuthority('SCOPE_internal.read')")
    public ResponseEntity<ProductResponse> getProductByIdInternal(@PathVariable("productId") Long productId) {
        ProductResponse product = productService.getProductById(productId);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    public Page<ProductResponse> getAllProducts(
            Pageable pageable,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand) {
        return productService.getAllProducts(pageable, category, brand);
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable("productId") Long productId, @RequestBody ProductRequest productRequest) {
        ProductResponse updatedProduct = productService.updateProduct(productId, productRequest);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProduct(@PathVariable("productId") Long productId) {
        productService.deleteProduct(productId);
    }

    @GetMapping("/{productId}/details")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ProductDetailsResponse> getProductDetails(@PathVariable("productId") Long productId) {
        ProductDetailsResponse productDetails = productService.getProductDetails(productId);
        return ResponseEntity.ok(productDetails);
    }
}
