package com.sumukh.rate.llimiter.service.service;

import com.sumukh.rate.llimiter.service.config.PropConfigs;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TokenBucketService {

    private final StringRedisTemplate redis;
    private final DefaultRedisScript<Long> script;
    private final PropConfigs configs;

    TokenBucketService(StringRedisTemplate redis, PropConfigs configs) {
        this.redis = redis;
        this.configs = configs;

        this.script = new DefaultRedisScript<>();
        this.script.setLocation(new ClassPathResource("token_bucket.lua"));
        this.script.setResultType(Long.class);
    }

    public boolean isAllowed(String clientId) {
        String tokenKey = "bucket:tokens:" + clientId;
        String tsKey = "bucket:ts:" + clientId;

        Long result = redis.execute(script,
                List.of(tokenKey, tsKey),
                configs.getCapacity(),
                configs.getRefillRate(),
                String.valueOf(System.currentTimeMillis()));

        return result != null && result == 1;
    }
}
