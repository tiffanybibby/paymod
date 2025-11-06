package com.tiffany.paymod.dto;

import com.tiffany.paymod.model.PaymentMethodStatus;

import java.time.LocalDateTime;

public record PaymentMethodDto(
        Long id,
        String label,
        String brand,
        String last4,
        Integer expMonth,
        Integer expYear,
        boolean isDefault,
        PaymentMethodStatus status,
        LocalDateTime createdAt
) {
}
