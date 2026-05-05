package com.sumukh.analytics_service.Event.consumer;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class EventConsumer {

    private final static Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    private final StringRedisTemplate redis;
    private final KafkaTemplate<Object, Object> template;
    private final MeterRegistry registry;

    public EventConsumer(StringRedisTemplate redis, KafkaTemplate<Object, Object> template, MeterRegistry registry) {
        this.redis = redis;
        this.template = template;
        this.registry = registry;
    }

    @KafkaListener(topics = "rate-limit-events", groupId = "analytics-group", concurrency = "3")
    public void consume(RequestEvent event, Acknowledgment ack) {
        try {
            process(event);
            ack.acknowledge();
        } catch (Exception ex) {
            logger.warn("Failed processing the event: {}, sending to retry-events-1", event);
            template.send("rate-limit-events-retry-1", event);
            ack.acknowledge();
        }
    }

    @KafkaListener(topics = "rate-limit-events-retry-1", groupId = "analytics-group")
    public void retry1(RequestEvent event) {
        try {
            process(event);
        } catch (Exception ex) {
            logger.warn("Failed processing the event: {}, sending to retry-events-2", event);
            template.send("rate-limit-events-retry-2", event);
        }
    }

    @KafkaListener(topics = "rate-limit-events-retry-2", groupId = "analytics-group")
    public void retry2(RequestEvent event) {
        try {
            process(event);
        } catch (Exception ex) {
            logger.warn("Failed processing the event: {}, sending to DLQ", event);
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

        Counter.builder("rate_limiter_requests")
                .tag("status", event.isAllowed() ? "ALLOWED" : "BLOCKED")
                .register(registry)
                .increment();

        logger.info("Processing the event {}", event); // Do business logic.
    }
}
