package com.ecommerce.userservice.client;

import com.ecommerce.common.config.FeignClientConfig; // Updated Import
import com.ecommerce.userservice.dto.NotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service", configuration = FeignClientConfig.class)
public interface NotificationClient {

    @PostMapping("/api/v1/notifications/send")
    void sendNotification(@RequestBody NotificationRequest request);
}
