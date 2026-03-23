package com.sumukh.rate.llimiter.service.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/limit")
public class RateLimiterController {

    @GetMapping("/check")
    public boolean isAllowed(@RequestParam String clientId) {
        return true;
    }
}
