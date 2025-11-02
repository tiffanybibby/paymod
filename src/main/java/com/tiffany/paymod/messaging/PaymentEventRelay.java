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
    public void onCreated(PaymentCreatedDomainEvent evt) {
        Payment payment = paymentRepository.findById(evt.paymentId()).orElseThrow();
        PaymentCreatedEvent payload = PaymentCreatedEvent.builder()
                .paymentId(payment.getId())
                .userId(payment.getUser().getId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getPaymentStatus().name())
                .createdAt(payment.getCreatedAt())
                .build();
        producer.send(createdTopic, String.valueOf(payment.getId()), payload);
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void onUpdated(PaymentStatusChangedDomainEvent evt) {
        Payment payment = paymentRepository.findById(evt.paymentId()).orElseThrow();
        PaymentUpdatedEvent payload = PaymentUpdatedEvent.builder()
                .paymentId(payment.getId())
                .userId(payment.getUser().getId())
                .status(payment.getPaymentStatus().name())
                .updatedAt(payment.getUpdatedAt())
                .build();
        producer.send(updatedTopic, String.valueOf(payment.getId()), payload);
    }
}
