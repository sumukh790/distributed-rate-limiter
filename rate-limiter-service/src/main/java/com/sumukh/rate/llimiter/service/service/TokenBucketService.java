package com.sumukh.rate.llimiter.service.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TokenBucketService {

    private final StringRedisTemplate redis;
    private final DefaultRedisScript<Long> script;

    TokenBucketService(StringRedisTemplate redis) {
        this.redis = redis;

        this.script = new DefaultRedisScript<>();
        this.script.setLocation(new ClassPathResource("token_bucket.lua"));
        this.script.setResultType(Long.class);
    }

    public boolean isAllowed(String clientId) {
        String tokenKey = "bucket:tokens:" + clientId;
        String tsKey = "bucket:ts:" + clientId;

        Long result = redis.execute(script,
                List.of(tokenKey, tsKey),
                "5",
                "5",
                String.valueOf(System.currentTimeMillis()));

        return result != null && result == 1;
    }
}
