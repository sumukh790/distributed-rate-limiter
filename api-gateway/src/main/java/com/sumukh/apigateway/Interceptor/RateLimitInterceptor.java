package com.sumukh.apigateway.Interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final WebClient webclient;

    RateLimitInterceptor(WebClient webclient) {
        this.webclient = webclient;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientId = request.getHeader("X-CLIENT-ID");

        if (clientId == null) {
            response.sendError(400, "Header X-CLIENT-ID missing");
            return false;
        }

        Boolean isAllowed = webclient.get().uri("/limit/check?clientId=" + clientId)
                .retrieve().bodyToMono(Boolean.class).block();

        if (Boolean.TRUE.equals(isAllowed)) {
            return true;
        } else {
            response.sendError(429, "Rate limit exceeded");
            return false;
        }
    }
}
