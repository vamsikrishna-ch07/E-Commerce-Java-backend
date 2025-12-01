package com.ecommerce.userservice.service;

import com.ecommerce.userservice.dto.UserRegistrationRequest;
import com.ecommerce.userservice.dto.UserResponse;

public interface UserService {
    UserResponse getUserById(Long id);
    UserResponse getUserByUsername(String username);
    UserResponse registerUser(UserRegistrationRequest request);
}
