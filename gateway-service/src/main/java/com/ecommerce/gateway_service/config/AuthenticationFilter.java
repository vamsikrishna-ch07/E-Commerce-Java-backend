package com.ecommerce.gateway_service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();

            // Public endpoints that do not require a token
            if (isPublicEndpoint(path)) {
                return chain.filter(exchange);
            }

            // Check for Authorization header
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No Authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                authHeader = authHeader.substring(7);
            }

            try {
                jwtUtil.validateToken(authHeader);
            } catch (Exception e) {
                return onError(exchange, "Unauthorized access: Invalid token", HttpStatus.UNAUTHORIZED);
            }

            // Role-based authorization
            String role = jwtUtil.extractRole(authHeader);
            if (!hasRequiredRole(role, path, request.getMethod().name())) {
                return onError(exchange, "Forbidden: Insufficient permissions", HttpStatus.FORBIDDEN);
            }

            return chain.filter(exchange);
        };
    }

    private boolean isPublicEndpoint(String path) {
        List<String> publicEndpoints = List.of(
                "/api/v1/auth/login",
                "/api/v1/users/register"
        );
        return publicEndpoints.stream().anyMatch(path::startsWith);
    }

    private boolean hasRequiredRole(String role, String path, String method) {
        // Example of a protected route: only admins can create products
        if (path.startsWith("/api/v1/products") && method.equals("POST")) {
            return "ROLE_ADMIN".equals(role);
        }
        // All other authenticated routes are accessible by default
        return true;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        // You can optionally write the error message to the response body
        // byte[] bytes = err.getBytes(StandardCharsets.UTF_8);
        // DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        // return exchange.getResponse().writeWith(Mono.just(buffer));
        return exchange.getResponse().setComplete();
    }

    public static class Config {
        // Configuration properties for the filter can be added here if needed
    }
}
