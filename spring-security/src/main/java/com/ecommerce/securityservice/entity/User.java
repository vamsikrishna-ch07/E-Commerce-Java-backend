package com.ecommerce.securityservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

// This is the final, correct DTO that matches the data from user-service.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    private Long id;
    private String username;
    private String password;
    private String role; // THIS FIELD WAS MISSING. IT IS NOW CORRECTLY INCLUDED.

    private boolean enabled = true;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == null) {
            return Collections.emptyList();
        }
        // Ensure the role is prefixed with "ROLE_" for consistency with hasRole() checks
        String authority = this.role.startsWith("ROLE_") ? this.role : "ROLE_" + this.role;
        return Collections.singletonList(new SimpleGrantedAuthority(authority));
    }

    // Boilerplate UserDetails methods
    @Override public boolean isAccountNonExpired() { return this.accountNonExpired; }
    @Override public boolean isAccountNonLocked() { return this.accountNonLocked; }
    @Override public boolean isCredentialsNonExpired() { return this.credentialsNonExpired; }
    @Override public boolean isEnabled() { return this.enabled; }
}
