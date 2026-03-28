package com.sumukh.analytics_service.Event.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventConsumer {

    @KafkaListener(topics = "rate-limit-events", groupId = "analytics-group", concurrency = "3")
    public void consume(RequestEvent event) {
        System.out.println("EVENT: " + event);
    }
}
