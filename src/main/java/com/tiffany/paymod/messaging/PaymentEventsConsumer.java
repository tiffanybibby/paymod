package com.tiffany.paymod.messaging;

import com.tiffany.paymod.events.PaymentCreatedEvent;
import com.tiffany.paymod.events.PaymentUpdatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentEventsConsumer {

    @KafkaListener(topics = "paymod.payments.created.v1", groupId = "paymod-dev")
    public void onCreated(PaymentCreatedEvent event) {
        log.info("Consumed PaymentCreatedEvent -> {}", event);
    }

    @KafkaListener(topics = "paymod.payments.updated.v1", groupId = "paymod-dev")
    public void onUpdated(PaymentUpdatedEvent event) {
        log.info("Consumed PaymentUpdatedEvent -> {}", event);
    }
}
