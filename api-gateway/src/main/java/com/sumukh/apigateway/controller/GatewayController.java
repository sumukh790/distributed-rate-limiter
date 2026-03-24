package com.sumukh.apigateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class GatewayController {

    @GetMapping("/test")
    public String getClient(@RequestHeader("X-CLIENT-ID") String clientId) {
        return "Do business logic only..";
    }
}