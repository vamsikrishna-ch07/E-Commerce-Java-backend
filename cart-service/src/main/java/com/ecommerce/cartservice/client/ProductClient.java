package com.ecommerce.cartservice.client;

import com.ecommerce.cartservice.dto.ProductResponse;
import com.ecommerce.common.config.InternalFeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", configuration = InternalFeignClientConfig.class)
public interface ProductClient {

    @GetMapping("/api/v1/products/internal/{productId}")
    ProductResponse getProductById(@PathVariable("productId") Long productId);
}
