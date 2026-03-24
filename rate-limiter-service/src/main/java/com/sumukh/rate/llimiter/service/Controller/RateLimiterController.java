package com.sumukh.rate.llimiter.service.Controller;

import com.sumukh.rate.llimiter.service.service.RedisRateLimitService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/limit")
public class RateLimiterController {

    private final RedisRateLimitService service;

    public RateLimiterController(RedisRateLimitService service) {
        this.service = service;
    }

    @GetMapping("/check")
    public boolean isAllowed(@RequestParam String clientId) {
        return service.isAllowed(clientId);
    }
}
