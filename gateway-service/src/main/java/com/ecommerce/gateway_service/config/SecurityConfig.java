package com.ecommerce.gateway_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(exchanges -> exchanges
                        // PUBLIC Routes
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/oauth2/token").permitAll()
                        .pathMatchers(HttpMethod.GET, "/.well-known/jwks.json").permitAll()

                        // Use the original service names as they appear in the URL
                        .pathMatchers(HttpMethod.POST, "/user-service/api/v1/users/register").permitAll() 
                        .pathMatchers(HttpMethod.GET, "/product-service/api/v1/products/**").permitAll()

                        // All other routes require authentication
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);

        return http.build();
    }
}
