package com.tiffany.paymod.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopics {

    @Bean
    public NewTopic paymentsCreatedTopic() {
        return TopicBuilder.name("paymod.payments.created.v1").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic paymentsUpdatedTopic() {
        return TopicBuilder.name("paymod.payments.updated.v1").partitions(3).replicas(1).build();
    }
}
