package com.sumukh.analytics_service.Event.consumer;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class EventConsumer {

    private final StringRedisTemplate redis;

    public EventConsumer(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @KafkaListener(
            topics = "rate-limit-events",
            groupId = "analytics-group",
            concurrency = "3"
    )
    public void consume(RequestEvent event, Acknowledgment ack) {
        String key = "event:" + event.getEventId();

        try {
            Boolean isNew = redis.opsForValue()
                    .setIfAbsent(key, event.getClientId(), Duration.ofHours(1));

            if (Boolean.FALSE.equals(isNew)) {
                ack.acknowledge();
                return;
            }

            System.out.println("EVENT: " + event);
            ack.acknowledge();

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
