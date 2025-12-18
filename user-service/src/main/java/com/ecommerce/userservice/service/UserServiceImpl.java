package com.ecommerce.userservice.service;

import com.ecommerce.userservice.client.NotificationClient;
import com.ecommerce.userservice.dto.*;
import com.ecommerce.userservice.model.Address;
import com.ecommerce.userservice.model.Role;
import com.ecommerce.userservice.model.User;
import com.ecommerce.userservice.repository.AddressRepository;
import com.ecommerce.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final NotificationClient notificationClient;
    private final PasswordEncoder passwordEncoder; // Inject the PasswordEncoder

    // Helper to map User entity to UserResponse DTO
    private UserResponse mapToUserResponse(User user) {
        List<AddressResponse> addressResponses = user.getAddresses().stream()
                .map(this::mapToAddressResponse)
                .collect(Collectors.toList());

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword()) // Still needed for Auth service to retrieve
                .role(user.getRole().name())
                .addresses(addressResponses)
                .build();
    }

    // Helper to map Address entity to AddressResponse DTO
    private AddressResponse mapToAddressResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .street(address.getStreet())
                .city(address.getCity())
                .state(address.getState())
                .zipCode(address.getZipCode())
                .country(address.getCountry())
                .build();
    }

    @Override
    @Transactional
    public UserResponse registerUser(UserRegistrationRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists!");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists!");
        }

        User newUser = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                // Encode the password before saving
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        User savedUser = userRepository.save(newUser);

        // Send a welcome email
        try {
            String emailBody = String.format("<h1>Welcome, %s!</h1><p>Thank you for registering with our e-commerce platform.</p>", savedUser.getUsername());
            NotificationRequest notificationRequest = NotificationRequest.builder()
                    .to(savedUser.getEmail())
                    .subject("Welcome to Our E-Commerce Platform!")
                    .body(emailBody)
                    .build();
            notificationClient.sendNotification(notificationRequest);
        } catch (Exception e) {
            // Log the error, but don't fail the registration if email fails
            System.err.println("Failed to send welcome email for user: " + savedUser.getUsername() + ". Error: " + e.getMessage());
        }

        return mapToUserResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        return mapToUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return mapToUserResponse(user);
    }

    @Override
    public User getUserEntityById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    @Override
    public UserResponse getMyProfile(Long userId) {
        User user = getUserEntityById(userId);
        return mapToUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateMyProfile(Long userId, UserProfileUpdateRequest request) {
        User user = getUserEntityById(userId);

        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                throw new RuntimeException("Username already taken!");
            }
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new RuntimeException("Email already taken!");
            }
            user.setEmail(request.getEmail());
        }

        User updatedUser = userRepository.save(user);
        return mapToUserResponse(updatedUser);
    }

    // THIS METHOD IS REMOVED as it's a security responsibility of the Auth Service
    // @Override
    // @Transactional
    // public void changeMyPassword(Long userId, PasswordChangeRequest request) { ... }

    @Override
    @Transactional
    public AddressResponse addAddress(Long userId, AddressRequest request) {
        User user = getUserEntityById(userId);

        Address address = Address.builder()
                .street(request.getStreet())
                .city(request.getCity())
                .state(request.getState())
                .zipCode(request.getZipCode())
                .country(request.getCountry())
                .user(user)
                .build();

        Address savedAddress = addressRepository.save(address);
        return mapToAddressResponse(savedAddress);
    }

    @Override
    public List<AddressResponse> getMyAddresses(Long userId) {
        User user = getUserEntityById(userId);
        return addressRepository.findByUserId(userId).stream()
                .map(this::mapToAddressResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new RuntimeException("Address not found or does not belong to user."));

        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setZipCode(request.getZipCode());
        address.setCountry(request.getCountry());

        Address updatedAddress = addressRepository.save(address);
        return mapToAddressResponse(updatedAddress);
    }

    @Override
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new RuntimeException("Address not found or does not belong to user."));
        addressRepository.delete(address);
    }
}
