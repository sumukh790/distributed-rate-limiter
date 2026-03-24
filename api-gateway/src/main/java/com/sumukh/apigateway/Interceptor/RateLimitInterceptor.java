package com.sumukh.apigateway.Interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RestTemplate template;

    RateLimitInterceptor(RestTemplate template) {
        this.template = template;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientId = request.getHeader("X-CLIENT-ID");

        if (clientId == null) {
            response.sendError(400, "Header X-CLIENT-ID missing");
            return false;
        }

        String url = "http://localhost:8081/limit/check?clientId=" + clientId;

        Boolean isAllowed = template.getForObject(url, Boolean.class);

        if (Boolean.TRUE.equals(isAllowed)) {
            return true;
        } else {
            response.sendError(429, "Rate limit exceeded");
            return false;
        }
    }
}
