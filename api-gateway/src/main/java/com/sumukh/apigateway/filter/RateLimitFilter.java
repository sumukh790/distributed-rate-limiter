package com.sumukh.apigateway.filter;

import com.sumukh.apigateway.event.RequestEvent;
import com.sumukh.apigateway.event.producer.EventProducer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class RateLimitFilter implements WebFilter {

    private final WebClient webClient;
    private final EventProducer producer;

    public RateLimitFilter(WebClient webClient, EventProducer producer) {
        this.webClient = webClient;
        this.producer = producer;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String clientId = exchange.getRequest()
                .getHeaders()
                .getFirst("X-CLIENT-ID");

        if (clientId == null) {
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            return exchange.getResponse()
                    .writeWith(Mono.just(exchange.getResponse()
                            .bufferFactory()
                            .wrap("Missing header".getBytes())));
        }

        return webClient.get()
                .uri("/limit/check?clientId=" + clientId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .flatMap(isAllowed -> {

                    Mono.fromRunnable(() -> producer.sendEvent(new RequestEvent(
                            clientId,
                            System.currentTimeMillis(),
                            isAllowed
                    ))).subscribe();

                    if (Boolean.TRUE.equals(isAllowed)) {
                        return chain.filter(exchange);
                    } else {
                        exchange.getResponse()
                                .setStatusCode(HttpStatus.TOO_MANY_REQUESTS);

                        return exchange.getResponse()
                                .writeWith(Mono.just(
                                        exchange.getResponse()
                                                .bufferFactory()
                                                .wrap("Rate limit exceeded".getBytes())
                                ));
                    }
                });
    }
}
