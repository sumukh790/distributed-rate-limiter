package com.sumukh.rate.llimiter.service.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/limit")
public class RateLimiterController {

    private final Map<String, Integer> counter = new ConcurrentHashMap<>();
    private long windowStart = System.currentTimeMillis();

    @GetMapping("/check")
    public boolean isAllowed(@RequestParam String clientId) {
        long now = System.currentTimeMillis();

        //Reset counter for 60 sec
        if (now - windowStart > 60000) {
            counter.clear();
            windowStart = now;
        }

        counter.putIfAbsent(clientId, 0);
        int count = counter.get(clientId);

        if (count >= 5) {
            return false;
        }

        counter.put(clientId, count+1);
        return true;
    }
}
