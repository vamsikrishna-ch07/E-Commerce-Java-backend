package com.ecommerce.orderservice.client;

import com.ecommerce.orderservice.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/api/v1/users/internal/id/{userId}") // Corrected to call the new internal endpoint by ID
    UserResponse getUserById(@PathVariable("userId") Long userId);
}
