package com.ecommerce.userservice.service;

import com.ecommerce.userservice.dto.*; // Import all DTOs
import com.ecommerce.userservice.model.User; // Import User model
import java.util.List;

public interface UserService {
    // Authentication/Registration related
    UserResponse registerUser(UserRegistrationRequest request);
    UserResponse getUserByUsername(String username); // Re-added for Spring Security
    UserResponse getUserById(Long userId); // Added for internal service calls

    // User Profile Management
    UserResponse getMyProfile(Long userId);
    UserResponse updateMyProfile(Long userId, UserProfileUpdateRequest request);
    // Removed: void changeMyPassword(Long userId, PasswordChangeRequest request);

    // Address Management
    AddressResponse addAddress(Long userId, AddressRequest request);
    List<AddressResponse> getMyAddresses(Long userId);
    AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request);
    void deleteAddress(Long userId, Long addressId);

    // Helper method to get User entity (for internal service use)
    User getUserEntityById(Long userId);
}
