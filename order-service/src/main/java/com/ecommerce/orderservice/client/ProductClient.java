package com.ecommerce.orderservice.client;

import com.ecommerce.common.config.InternalFeignClientConfig;
import com.ecommerce.orderservice.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", configuration = InternalFeignClientConfig.class)
public interface ProductClient {

    @GetMapping("/api/v1/products/{productId}")
    ProductResponse getProductById(@PathVariable("productId") Long productId);
}
