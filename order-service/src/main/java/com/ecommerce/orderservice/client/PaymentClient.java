package com.ecommerce.orderservice.client;

import com.ecommerce.common.config.InternalFeignClientConfig;
import com.ecommerce.orderservice.dto.PaymentRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", configuration = InternalFeignClientConfig.class)
public interface PaymentClient {

    @PostMapping("/api/v1/payments/process")
    void processPayment(@RequestBody PaymentRequest request);
}
