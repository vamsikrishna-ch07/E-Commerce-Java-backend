package com.ecommerce.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    /**
     * This bean configures a WebClient that automatically includes an OAuth2 token.
     * It now correctly depends on the 'authorizedClientManager' bean that is
     * created and managed in FeignClientConfig.java, resolving the conflict.
     */
    @Bean
    public WebClient webClient(OAuth2AuthorizedClientManager authorizedClientManager) {
        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2Client =
                new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        
        // This default is used when a specific client registration is not specified in the call.
        oauth2Client.setDefaultClientRegistrationId("user-service-client"); 

        return WebClient.builder()
                .filter(oauth2Client)
                .build();
    }

    // The duplicate 'authorizedClientManager' bean has been REMOVED from this class
    // to resolve the BeanDefinitionOverrideException. The single source of truth
    // for this bean is now FeignClientConfig.java.
}
