package com.ecommerce.wishlistservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Configuration
public class FeignClientInterceptor implements RequestInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TOKEN_TYPE = "Bearer";

    @Autowired
    private OAuth2AuthorizedClientManager authorizedClientManager;

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Override
    public void apply(RequestTemplate template) {
        // We need to get the registration for the client we want to use.
        // The name "wishlist-service-client" should match what's in your application.yml or centralized config.
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("wishlist-service-client");

        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId(clientRegistration.getRegistrationId())
                .principal("wishlist-service") // The principal name can be anything for client_credentials
                .build();

        // Use the authorizedClientManager to get the token.
        // This will handle fetching a new token or reusing an existing one.
        OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(authorizeRequest);

        if (authorizedClient != null && authorizedClient.getAccessToken() != null) {
            String tokenValue = authorizedClient.getAccessToken().getTokenValue();
            template.header(AUTHORIZATION_HEADER, String.format("%s %s", BEARER_TOKEN_TYPE, tokenValue));
        }
    }
}
