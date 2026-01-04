package com.ecommerce.inventoryservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt
                        .jwtAuthenticationConverter(jwtAuthenticationConverter())
                ))
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        // This converter will extract authorities from the "scope" or "scp" claim.
        JwtGrantedAuthoritiesConverter scopeAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        // This converter will extract authorities from our custom "authorities" claim.
        JwtGrantedAuthoritiesConverter claimAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        claimAuthoritiesConverter.setAuthoritiesClaimName("authorities");
        // We don't want the default "SCOPE_" prefix for our roles.
        claimAuthoritiesConverter.setAuthorityPrefix("");

        // Create a new converter that will merge the authorities from both sources.
        // This ensures that both SCOPE_ and ROLE_ authorities are correctly processed.
        var authoritiesConverter = new DelegatingJwtGrantedAuthoritiesConverter(
                scopeAuthoritiesConverter,
                claimAuthoritiesConverter
        );

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return jwtAuthenticationConverter;
    }
}

// Helper class to delegate to multiple converters
class DelegatingJwtGrantedAuthoritiesConverter implements org.springframework.core.convert.converter.Converter<org.springframework.security.oauth2.jwt.Jwt, java.util.Collection<org.springframework.security.core.GrantedAuthority>> {

    private final java.util.List<org.springframework.core.convert.converter.Converter<org.springframework.security.oauth2.jwt.Jwt, java.util.Collection<org.springframework.security.core.GrantedAuthority>>> converters;

    public DelegatingJwtGrantedAuthoritiesConverter(org.springframework.core.convert.converter.Converter<org.springframework.security.oauth2.jwt.Jwt, java.util.Collection<org.springframework.security.core.GrantedAuthority>>... converters) {
        this.converters = java.util.Arrays.asList(converters);
    }

    @Override
    public java.util.Collection<org.springframework.security.core.GrantedAuthority> convert(org.springframework.security.oauth2.jwt.Jwt jwt) {
        return this.converters.stream()
                .flatMap(converter -> converter.convert(jwt).stream())
                .collect(java.util.stream.Collectors.toSet());
    }
}