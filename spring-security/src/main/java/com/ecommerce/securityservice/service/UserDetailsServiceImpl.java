package com.ecommerce.securityservice.service;

import com.ecommerce.securityservice.client.UserClient;
import com.ecommerce.securityservice.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserClient userClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserResponse userResponse = userClient.getUserByUsername(username);

        if (userResponse == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        return new User(userResponse.getUsername(), userResponse.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(userResponse.getRole())));
    }
}
