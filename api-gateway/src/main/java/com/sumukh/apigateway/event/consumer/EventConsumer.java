package com.sumukh.apigateway.event.consumer;

import com.sumukh.apigateway.event.RequestEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventConsumer {

    @KafkaListener(topics = "rate-limit-events", groupId = "analytics-group")
    public void consume (RequestEvent requestEvent) {
        System.out.println("EVENT: " + requestEvent.getClientId() +
                " allowed=" + requestEvent.isAllowed());
    }
}
