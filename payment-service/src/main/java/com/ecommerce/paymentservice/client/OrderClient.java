package com.ecommerce.paymentservice.client;

import com.ecommerce.common.config.InternalFeignClientConfig;
import com.ecommerce.paymentservice.dto.OrderStatusUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "order-service", configuration = InternalFeignClientConfig.class)
public interface OrderClient {

    @PutMapping("/api/v1/orders/{orderId}/status")
    void updateOrderStatus(@PathVariable("orderId") Long orderId, @RequestBody OrderStatusUpdateRequest request);
}
