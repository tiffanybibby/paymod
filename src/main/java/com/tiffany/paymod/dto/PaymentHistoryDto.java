package com.tiffany.paymod.dto;

import com.tiffany.paymod.model.PaymentEventType;
import com.tiffany.paymod.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentHistoryDto(
        Long id,
        Long paymentId,
        PaymentStatus oldStatus,
        PaymentStatus newStatus,
        BigDecimal amount,
        String currency,
        PaymentEventType eventType,
        Instant occurredAt
) {
}
