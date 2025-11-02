package com.tiffany.paymod.events;

import com.tiffany.paymod.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCreatedEvent {
    private String eventId;
    private Instant occurredAt;
    private Long paymentId;
    private Long userId;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
}
