package com.sumukh.analytics_service.Event.consumer;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class EventConsumer {

    private final StringRedisTemplate redis;
    private final KafkaTemplate<Object, Object> template;

    public EventConsumer(StringRedisTemplate redis, KafkaTemplate<Object, Object> template) {
        this.redis = redis;
        this.template = template;
    }

    @KafkaListener(topics = "rate-limit-events", groupId = "analytics-group", concurrency = "3")
    public void consume(RequestEvent event, Acknowledgment ack) {
        try {
            process(event);
            ack.acknowledge();
        } catch (Exception ex) {
            template.send("rate-limit-events-retry-1", event);
            ack.acknowledge();
        }
    }

    @KafkaListener(topics = "rate-limit-events-retry-1", groupId = "analytics-group")
    public void retry1(RequestEvent event) {
        try {
            process(event);
        } catch (Exception ex) {
            template.send("rate-limit-events-retry-2", event);
        }
    }

    @KafkaListener(topics = "rate-limit-events-retry-2", groupId = "analytics-group")
    public void retry2(RequestEvent event) {
        try {
            process(event);
        } catch (Exception ex) {
            template.send("rate-limit-events-dlq", event);
        }
    }

    private void process(RequestEvent event) {
        String key = "event:" + event.getEventId();
        Boolean isNew = redis.opsForValue()
                .setIfAbsent(key, event.getClientId(), Duration.ofHours(1));

        if (Boolean.FALSE.equals(isNew)) {
            return;
        }

        System.out.println("PROCESSING: " + event);
    }
}
