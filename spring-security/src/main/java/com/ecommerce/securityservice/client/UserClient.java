package com.ecommerce.securityservice.client;

import com.ecommerce.securityservice.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/api/v1/users/username/{username}")
    UserResponse getUserByUsername(@PathVariable("username") String username);
}
