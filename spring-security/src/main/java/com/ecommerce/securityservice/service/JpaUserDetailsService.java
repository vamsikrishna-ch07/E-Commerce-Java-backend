package com.ecommerce.securityservice.service;

import com.ecommerce.securityservice.client.UserClient;
import com.ecommerce.securityservice.entity.User;
import com.ecommerce.securityservice.entity.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(JpaUserDetailsService.class);
    private final UserClient userClient;
    private final PasswordEncoder passwordEncoder;

    public JpaUserDetailsService(UserClient userClient, PasswordEncoder passwordEncoder) {
        this.userClient = userClient;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Attempting to load user by username: {}", username);
        UserResponse userResponse;
        try {
            userResponse = userClient.loadUserByUsername(username);
        } catch (Exception e) {
            log.error("Error calling user-service for username: {}", username, e);
            throw new UsernameNotFoundException("Error fetching user details from user-service.", e);
        }

        if (userResponse == null) {
            log.warn("User not found from user-service for username: {}", username);
            throw new UsernameNotFoundException("User not found: " + username);
        }

        log.info("Successfully fetched user from user-service: {}", userResponse.getUsername());
        log.info("Role received from user-service: {}", userResponse.getRole()); // NEW LOG
        log.info("Fetched encoded password: [PROTECTED]"); // Never log the full password hash

        // Temporary hardcoded password check (for debugging purposes)
        // Assuming the password you're trying to log in with is "password123"
        boolean passwordMatches = passwordEncoder.matches("password123", userResponse.getPassword());
        log.info("Password 'password123' matches fetched password: {}", passwordMatches);


        // Map UserResponse DTO to the User entity, explicitly setting UserDetails flags to true
        return User.builder()
                .id(userResponse.getId())
                .username(userResponse.getUsername())
                .password(userResponse.getPassword()) // The encoded password from the user-service
                .role(userResponse.getRole())
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();
    }
}
