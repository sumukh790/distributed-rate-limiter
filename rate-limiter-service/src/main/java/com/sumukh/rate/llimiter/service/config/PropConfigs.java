package com.sumukh.rate.llimiter.service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "base")
public class PropConfigs {

    private String refillRate;
    private String capacity;

    public String getRefillRate() {
        return refillRate;
    }

    public void setRefillRate(String refillRate) {
        this.refillRate = refillRate;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }
}
