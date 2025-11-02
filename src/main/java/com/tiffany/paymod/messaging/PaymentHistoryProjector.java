package com.tiffany.paymod.messaging;

import com.tiffany.paymod.events.PaymentCreatedEvent;
import com.tiffany.paymod.events.PaymentUpdatedEvent;
import com.tiffany.paymod.model.Payment;
import com.tiffany.paymod.model.PaymentEventType;
import com.tiffany.paymod.model.PaymentHistory;
import com.tiffany.paymod.repository.PaymentHistoryRepository;
import com.tiffany.paymod.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentHistoryProjector {
    private final PaymentRepository paymentRepository;
    private final PaymentHistoryRepository historyRepository;

    @Transactional
    @KafkaListener(topics = "paymod.payments.created.v1", groupId = "paymod-history")
    public void onPaymentCreated(PaymentCreatedEvent event) {
        if (historyRepository.existsByEventId(event.getEventId())) return;
        Payment payment = paymentRepository.findById(event.getPaymentId())
                .orElseThrow(() -> new IllegalStateException("Payment not found: " + event.getPaymentId()));
        PaymentHistory paymentHistory = new PaymentHistory();
        paymentHistory.setPayment(payment);
        paymentHistory.setEventType(PaymentEventType.PAYMENT_CREATED);
        paymentHistory.setEventId(event.getEventId());
        paymentHistory.setOccurredAt(event.getOccurredAt());
        paymentHistory.setNewStatus(event.getStatus());
        paymentHistory.setAmount(event.getAmount());
        paymentHistory.setCurrency(event.getCurrency());

        historyRepository.save(paymentHistory);
        log.info("History recorded for created paymentId={}, eventId={}", event.getPaymentId(), event.getEventId());
    }

    @Transactional
    @KafkaListener(topics = "paymod.payments.updated.v1", groupId = "paymod-history")
    public void onPaymentUpdated(PaymentUpdatedEvent event) {
        if (historyRepository.existsByEventId(event.getEventId())) return;
        Payment payment = paymentRepository.findById(event.getPaymentId())
                .orElseThrow(() -> new IllegalStateException("Payment not found: " + event.getPaymentId()));
        PaymentHistory paymentHistory = new PaymentHistory();
        paymentHistory.setPayment(payment);
        paymentHistory.setEventType(PaymentEventType.PAYMENT_STATUS_UPDATED);
        paymentHistory.setEventId(event.getEventId());
        paymentHistory.setOccurredAt(event.getOccurredAt());
        paymentHistory.setOldStatus(event.getOldStatus());
        paymentHistory.setNewStatus(event.getNewStatus());
        paymentHistory.setAmount(event.getAmount());
        paymentHistory.setCurrency(event.getCurrency());

        historyRepository.save(paymentHistory);
        log.info("History recorded for updated paymentId={}, {} -> {}, eventId={}",
                event.getPaymentId(), event.getOldStatus(), event.getNewStatus(), event.getEventId());
    }
}
