package com.ecommerce.common.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InternalServiceAuthInterceptor implements RequestInterceptor {

    @Value("${jwt.secret}")
    private String secret;

    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    @Override
    public void apply(RequestTemplate template) {
        String token = generateInternalServiceToken();
        template.header("Authorization", "Bearer " + token);
    }

    private String generateInternalServiceToken() {
        long now = System.currentTimeMillis();
        
        // Create authorities in the format expected by JwtAuthenticationFilter: [{"authority": "ROLE_..."}]
        List<String> scopes = List.of("SCOPE_internal.read", "SCOPE_internal.write");
        List<Map<String, String>> authorities = scopes.stream()
                .map(scope -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("authority", scope);
                    return map;
                })
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject("internal-service")
                .claim("authorities", authorities)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + 60 * 1000)) // 1 minute expiration
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
