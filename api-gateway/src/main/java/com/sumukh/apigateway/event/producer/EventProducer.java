package com.sumukh.apigateway.event.producer;

import com.sumukh.apigateway.event.RequestEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventProducer {

    private final KafkaTemplate<String, RequestEvent> template;

    EventProducer(KafkaTemplate<String, RequestEvent> template) {
        this.template = template;
    }

    public void sendEvent(RequestEvent requestEvent) {
        template.send("rate-limit-events", requestEvent.getClientId(), requestEvent);
    }
}
