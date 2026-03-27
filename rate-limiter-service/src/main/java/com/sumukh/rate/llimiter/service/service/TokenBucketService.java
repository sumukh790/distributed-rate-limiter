package com.sumukh.rate.llimiter.service.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TokenBucketService {

    private final StringRedisTemplate template;

    TokenBucketService(StringRedisTemplate template) {
        this.template = template;
    }

    public boolean isAllowed(String clientId) {
        String tokenKey = "bucket:tokens:" + clientId;
        String tsKey = "bucket:ts:" + clientId;

        int capacity = 5;
        int refillRate = 5;

        long now = System.currentTimeMillis();
        String lastTokenTsStr = template.opsForValue().get(tsKey);
        Long lastTs = lastTokenTsStr == null ? now : Long.parseLong(lastTokenTsStr);

        long timeElapsed = now - lastTs;

        int refill = (int) (timeElapsed / 60000.0 * refillRate);
        String currentToken = template.opsForValue().get(tokenKey);
        int token = currentToken == null ? capacity : Integer.parseInt(currentToken);

        token = Math.min(capacity, token + refill);

        if (token == 0) return false;

        template.opsForValue().set(tokenKey, String.valueOf(token - 1));
        template.opsForValue().set(tsKey, String.valueOf(now));

        return true;
    }

}
