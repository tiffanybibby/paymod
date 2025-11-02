package com.tiffany.paymod.messaging;

import com.tiffany.paymod.domain.PaymentCreatedDomainEvent;
import com.tiffany.paymod.domain.PaymentStatusChangedDomainEvent;
import com.tiffany.paymod.events.PaymentCreatedEvent;
import com.tiffany.paymod.events.PaymentUpdatedEvent;
import com.tiffany.paymod.model.Payment;
import com.tiffany.paymod.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;
import java.util.UUID;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Component
@RequiredArgsConstructor
public class PaymentEventRelay {

    private final PaymentRepository paymentRepository;
    private final PaymentEventsProducer producer;

    @Value("${paymod.topics.payments-created}")
    private String createdTopic;

    @Value("${paymod.topics.payments-updated}")
    private String updatedTopic;

    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void onCreated(PaymentCreatedDomainEvent event) {
        Instant now = Instant.now();
        Payment payment = paymentRepository.findById(event.paymentId()).orElseThrow();
        PaymentCreatedEvent payload = PaymentCreatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .occurredAt(now)
                .paymentId(payment.getId())
                .userId(payment.getUser().getId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getPaymentStatus())
                .build();
        producer.send(createdTopic, String.valueOf(payment.getId()), payload);
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void onUpdated(PaymentStatusChangedDomainEvent event) {
        Instant now = Instant.now();
        Payment payment = paymentRepository.findById(event.paymentId()).orElseThrow();
        PaymentUpdatedEvent payload = PaymentUpdatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .occurredAt(now)
                .paymentId(payment.getId())
                .userId(payment.getUser().getId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .oldStatus(event.oldStatus())
                .newStatus(event.newStatus())
                .build();
        producer.send(updatedTopic, String.valueOf(payment.getId()), payload);
    }
}
