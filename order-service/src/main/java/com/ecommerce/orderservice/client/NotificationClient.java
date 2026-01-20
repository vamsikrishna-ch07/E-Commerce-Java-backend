package com.ecommerce.orderservice.client;

import com.ecommerce.common.config.InternalFeignClientConfig;
import com.ecommerce.orderservice.dto.NotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service", configuration = InternalFeignClientConfig.class)
public interface NotificationClient {

    @PostMapping("/api/v1/notifications/send")
    void sendNotification(@RequestBody NotificationRequest request);
}
