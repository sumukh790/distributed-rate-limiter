package com.sumukh.apigateway.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class RateLimitClient {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitClient.class);
    private final WebClient webClient;

    public RateLimitClient(WebClient webClient) {
        this.webClient = webClient;
    }

    @CircuitBreaker(name = "rateLimiterService")
    public Mono<Boolean> checkLimit(String clientId) {
        return webClient.get()
                .uri("/limit/check?clientId=" + clientId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .timeout(Duration.ofSeconds(1))
                .onErrorResume(ex -> fallback(clientId, ex));
    }

    private Mono<Boolean> fallback(String clientId, Throwable ex) {
        logger.info("Circuit breaker fallback triggered for the client: {} due to : {}", clientId, ex.getMessage());
        return Mono.just(false);
    }
}
