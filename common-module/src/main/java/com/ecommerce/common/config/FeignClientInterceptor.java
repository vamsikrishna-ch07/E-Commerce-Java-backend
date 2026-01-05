package com.ecommerce.common.config;

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
        // The name "service-client" is a generic name we will use for all internal calls.
        // Each service's application.yml should define a registration with this name,
        // OR we can make this dynamic. For now, let's assume "service-client" is the standard.
        // If a service has a specific client ID (like "cart-service-client"), we might need a way to look it up.
        // A simple fallback is to try "service-client" if a specific one isn't found, or just enforce "service-client" everywhere.
        
        String registrationId = "service-client"; 
        
        // Check if a specific registration exists (optional enhancement, sticking to simple for now)
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);
        
        if (clientRegistration == null) {
             // Fallback or error handling. For this refactoring, we assume the config exists.
             return; 
        }

        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId(clientRegistration.getRegistrationId())
                .principal("internal-service") 
                .build();

        OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(authorizeRequest);

        if (authorizedClient != null && authorizedClient.getAccessToken() != null) {
            String tokenValue = authorizedClient.getAccessToken().getTokenValue();
            template.header(AUTHORIZATION_HEADER, String.format("%s %s", BEARER_TOKEN_TYPE, tokenValue));
        }
    }
}
