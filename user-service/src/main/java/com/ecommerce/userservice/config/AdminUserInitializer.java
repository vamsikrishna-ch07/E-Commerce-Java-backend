package com.ecommerce.userservice.config;

import com.ecommerce.userservice.model.Role;
import com.ecommerce.userservice.model.User;
import com.ecommerce.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if an admin user already exists
        if (userRepository.findByRole(Role.ROLE_ADMIN).isEmpty()) {
            User adminUser = User.builder()
                    .username("admin")
                    .email("admin@ecommerce.com")
                    .password(passwordEncoder.encode("adminpass")) // Use a strong password in production
                    .role(Role.ROLE_ADMIN)
                    .build();
            userRepository.save(adminUser);
            System.out.println("Admin user 'admin' created with password 'adminpass'");
        }
    }
}
