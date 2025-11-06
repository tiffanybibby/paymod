package com.tiffany.paymod.dto;

import com.tiffany.paymod.model.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentHistoryDto(
        Long id,
        Long paymentId,
        PaymentStatus oldStatus,
        PaymentStatus newStatus,
        String eventType,
        String reasonCode,
        String reasonMessage,
        LocalDateTime occurredAt
) {
}
