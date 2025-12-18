package com.ecommerce.productservice.service;

import com.ecommerce.productservice.client.InventoryClient;
import com.ecommerce.productservice.dto.InventoryResponse;
import com.ecommerce.productservice.dto.ProductDetailsResponse;
import com.ecommerce.productservice.dto.ProductRequest;
import com.ecommerce.productservice.dto.ProductResponse;
import com.ecommerce.productservice.model.Product;
import com.ecommerce.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final InventoryClient inventoryClient;

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .brand(productRequest.getBrand())
                .category(productRequest.getCategory())
                .price(productRequest.getPrice())
                .imageUrl(productRequest.getImageUrl())
                .build();
        Product savedProduct = productRepository.save(product);
        return mapToProductResponse(savedProduct);
    }

    @Override
    public ProductResponse getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        return mapToProductResponse(product);
    }

    @Override
    public Page<ProductResponse> getAllProducts(Pageable pageable, String category, String brand) {
        Specification<Product> spec = Specification.where(null);

        if (StringUtils.hasText(category)) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("category"), category));
        }
        if (StringUtils.hasText(brand)) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("brand"), brand));
        }

        return productRepository.findAll(spec, pageable) // Fix: Added missing semicolon
                .map(this::mapToProductResponse);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long productId, ProductRequest productRequest) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setBrand(productRequest.getBrand());
        product.setCategory(productRequest.getCategory());
        product.setPrice(productRequest.getPrice());
        product.setImageUrl(productRequest.getImageUrl());

        Product updatedProduct = productRepository.save(product);
        return mapToProductResponse(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }

    @Override
    public ProductDetailsResponse getProductDetails(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        Integer stockQuantity = 0;
        try {
            InventoryResponse inventory = inventoryClient.getInventoryByProductId(productId);
            if (inventory != null) {
                stockQuantity = inventory.getQuantity();
            }
        } catch (Exception e) {
            log.error("Unable to retrieve inventory for productId: {}. Defaulting to quantity 0. Error: {}", productId, e.getMessage());
        }

        return ProductDetailsResponse.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .description(product.getDescription())
                .brand(product.getBrand())
                .category(product.getCategory())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .stockQuantity(stockQuantity)
                .build();
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .description(product.getDescription())
                .brand(product.getBrand())
                .category(product.getCategory())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .build();
    }
}
