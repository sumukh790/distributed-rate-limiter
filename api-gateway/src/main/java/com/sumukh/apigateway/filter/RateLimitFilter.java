package com.sumukh.apigateway.filter;

import com.sumukh.apigateway.event.RequestEvent;
import com.sumukh.apigateway.event.producer.EventProducer;
import com.sumukh.apigateway.service.RateLimitClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class RateLimitFilter implements WebFilter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitFilter.class);
    private final RateLimitClient client;
    private final EventProducer producer;

    public RateLimitFilter(RateLimitClient client, EventProducer producer) {
        this.client = client;
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

        return client.checkLimit(clientId)
                .flatMap(isAllowed -> {

                    logger.info("Sending request event for the clientId: {}", clientId);
                    producer.sendEvent(new RequestEvent(
                            clientId,
                            System.currentTimeMillis(),
                            isAllowed,
                            clientId + System.currentTimeMillis()
                    ));

                    if (Boolean.TRUE.equals(isAllowed)) {
                        return chain.filter(exchange);
                    } else {
                        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);

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
