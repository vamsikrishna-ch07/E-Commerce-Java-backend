package com.ecommerce.securityservice.config;

import feign.RequestInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;

@Configuration
public class FeignClientConfig {

    private static final Logger log = LoggerFactory.getLogger(FeignClientConfig.class);

    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository authorizedClientRepository) {

        OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .clientCredentials()
                        .build();

        DefaultOAuth2AuthorizedClientManager authorizedClientManager =
                new DefaultOAuth2AuthorizedClientManager(
                        clientRegistrationRepository, authorizedClientRepository);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }

    @Bean
    public RequestInterceptor requestInterceptor(OAuth2AuthorizedClientManager authorizedClientManager) {
        return requestTemplate -> {
            log.debug("Feign RequestInterceptor: Attempting to get token for client registration 'auth-service-client'");
            OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                    .withClientRegistrationId("auth-service-client")
                    .principal("spring-security") // Principal name for client_credentials flow
                    .build();

            OAuth2AuthorizedClient authorizedClient = null;
            try {
                authorizedClient = authorizedClientManager.authorize(authorizeRequest);
            } catch (Exception e) {
                log.error("Feign RequestInterceptor: Error authorizing client 'auth-service-client'", e);
            }


            if (authorizedClient != null && authorizedClient.getAccessToken() != null) {
                String tokenValue = authorizedClient.getAccessToken().getTokenValue();
                requestTemplate.header("Authorization", "Bearer " + tokenValue);
                log.debug("Feign RequestInterceptor: Successfully added Bearer token to request. Token starts with: {}", tokenValue.substring(0, Math.min(tokenValue.length(), 20)));
            } else {
                log.warn("Feign RequestInterceptor: No access token obtained for client registration 'auth-service-client'. Request will proceed without Authorization header.");
            }
        };
    }
}
