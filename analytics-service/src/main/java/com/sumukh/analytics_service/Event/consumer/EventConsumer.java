package com.sumukh.analytics_service.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventConsumer {

    @KafkaListener(topics = "rate-limit-events", groupId = "analytics-group")
    public void consume (Object requestEvent) {
        System.out.println("Analytics received: " + requestEvent);
    }
}
