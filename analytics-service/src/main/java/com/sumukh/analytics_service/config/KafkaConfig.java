package com.sumukh.analytics_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic dqlTopic() {
        return TopicBuilder.name("rate-limit-events-dlq")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> template) {
        DeadLetterPublishingRecoverer recoverr = new DeadLetterPublishingRecoverer(
                template, (record, ex) -> new TopicPartition
                (record.topic() + "-dlq", record.partition())
        );

        return new DefaultErrorHandler(recoverr, new FixedBackOff(1000l, 3));
    }
}
