package com.sumukh.rate.llimiter.service.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisRateLimitService {

    private final StringRedisTemplate template;

    RedisRateLimitService(StringRedisTemplate template) {
        this.template = template;
    }

    public boolean isAllowed(String clientId) {
        String key = "client_id:" + clientId;

        Long count = template.opsForValue().increment(key);
        if (count == 1) {
            template.expire(key, Duration.ofSeconds(60));
        }

        return count <= 5;
    }
}
