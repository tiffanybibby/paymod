package com.tiffany.paymod.domain;

import com.tiffany.paymod.model.PaymentStatus;

public record PaymentStatusChangedDomainEvent(
        Long paymentId,
        PaymentStatus oldStatus,
        PaymentStatus newStatus
){}
