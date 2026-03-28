package com.sumukh.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

    @Bean
    public WebClient webClient(PropConfig config) {
        return WebClient.builder()
                .baseUrl(config.getBaseUrl())
                .build();
    }
}
