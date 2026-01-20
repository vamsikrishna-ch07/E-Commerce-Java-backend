package com.ecommerce.userservice.controller;

import com.ecommerce.userservice.dto.*;
import com.ecommerce.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Helper to get the authenticated user's ID from the token
    private Long getUserId(Principal principal) {
        // In our setup, the 'sub' (subject) claim of the JWT is the user's ID.
        // Spring Security makes this available via principal.getName().
        return Long.valueOf(principal.getName());
    }

    // PUBLIC ENDPOINT
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody UserRegistrationRequest request) {
        UserResponse newUser = userService.registerUser(request);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }
    
    // PUBLIC ENDPOINT FOR LOGIN
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        String token = userService.login(request);
        return ResponseEntity.ok(token);
    }

    // INTERNAL-ONLY ENDPOINT (for spring-security service)
    @GetMapping("/internal/{username}")
    @PreAuthorize("hasAuthority('SCOPE_internal.read')")
    public UserResponse getUserByUsername(@PathVariable("username") String username) {
        return userService.getUserByUsername(username);
    }

    // INTERNAL-ONLY ENDPOINT (for other services)
    @GetMapping("/internal/id/{userId}")
    @PreAuthorize("hasRole('USER') or hasAuthority('SCOPE_internal.read')") // Allow if it's a user OR an internal service
    public UserResponse getUserByIdInternal(@PathVariable("userId") Long userId) {
        return userService.getUserById(userId);
    }

    // === USER PROFILE ENDPOINTS (RESTORED) ===

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponse> getMyProfile(Principal principal) {
        UserResponse userProfile = userService.getMyProfile(getUserId(principal));
        return ResponseEntity.ok(userProfile);
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponse> updateMyProfile(Principal principal, @RequestBody UserProfileUpdateRequest request) {
        UserResponse updatedUser = userService.updateMyProfile(getUserId(principal), request);
        return ResponseEntity.ok(updatedUser);
    }

    // === ADDRESS ENDPOINTS (RESTORED) ===

    @PostMapping("/me/addresses")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AddressResponse> addAddress(Principal principal, @RequestBody AddressRequest request) {
        AddressResponse newAddress = userService.addAddress(getUserId(principal), request);
        return new ResponseEntity<>(newAddress, HttpStatus.CREATED);
    }

    @GetMapping("/me/addresses")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<AddressResponse>> getMyAddresses(Principal principal) {
        List<AddressResponse> addresses = userService.getMyAddresses(getUserId(principal));
        return ResponseEntity.ok(addresses);
    }

    @PutMapping("/me/addresses/{addressId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AddressResponse> updateAddress(Principal principal, @PathVariable("addressId") Long addressId, @RequestBody AddressRequest request) {
        AddressResponse updatedAddress = userService.updateAddress(getUserId(principal), addressId, request);
        return ResponseEntity.ok(updatedAddress);
    }



    @DeleteMapping("/me/addresses/{addressId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteAddress(Principal principal, @PathVariable("addressId") Long addressId) {
        userService.deleteAddress(getUserId(principal), addressId);
        return ResponseEntity.noContent().build();
    }
}
