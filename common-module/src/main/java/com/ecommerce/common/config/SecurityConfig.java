package com.ecommerce.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Stream;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/actuator/**").permitAll()
                        // Allow registration endpoint publicly
                        .requestMatchers("/api/v1/users/register").permitAll()
                        // Allow public product viewing (if needed globally, or handle in specific services)
                        .requestMatchers("/api/v1/products/**").permitAll() 
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt
                        .jwtAuthenticationConverter(jwtAuthenticationConverter())
                ))
                .csrf(csrf -> csrf.disable()); // Disable CSRF for API endpoints
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new CustomJwtGrantedAuthoritiesConverter());
        return jwtAuthenticationConverter;
    }
}

class CustomJwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Collection<GrantedAuthority> grantedAuthorities = new HashSet<>();

        // Extract SCOPE_ authorities from the 'scope' claim
        String scope = jwt.getClaimAsString("scope");
        if (scope != null) {
            Stream.of(scope.split(" "))
                    .map(scopeName -> "SCOPE_" + scopeName)
                    .map(SimpleGrantedAuthority::new)
                    .forEach(grantedAuthorities::add);
        }

        // Extract ROLE_ authorities from the 'authorities' claim
        Collection<String> authorities = jwt.getClaimAsStringList("authorities");
        if (authorities != null) {
            authorities.stream()
                    .map(roleName -> {
                        if (roleName.startsWith("ROLE_")) {
                            return new SimpleGrantedAuthority(roleName);
                        }
                        // If roles don't start with ROLE_, add it for consistency with hasRole()
                        return new SimpleGrantedAuthority("ROLE_" + roleName);
                    })
                    .forEach(grantedAuthorities::add);
        }

        return grantedAuthorities;
    }
}
