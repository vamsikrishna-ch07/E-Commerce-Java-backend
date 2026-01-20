package com.ecommerce.common.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

public class InternalFeignClientConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new InternalServiceAuthInterceptor();
    }
}
