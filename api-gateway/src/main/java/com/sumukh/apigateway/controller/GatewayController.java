package com.sumukh.apigateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
public class GatewayController {

    private final RestTemplate restTemplate;

    public GatewayController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/test")
    public String getClient(@RequestHeader("X-CLIENT-ID") String clientId) {
        String url = "http://localhost:8081/limit/check?clientId=" + clientId;
        Boolean allowed = restTemplate.getForObject(url, Boolean.class);

        if (Boolean.TRUE.equals(allowed)) {
            return "Can enter";
        } else {
            return "Rate limited...";
        }
    }
}
