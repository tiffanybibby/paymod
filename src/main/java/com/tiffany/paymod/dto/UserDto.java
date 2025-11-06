package com.tiffany.paymod.dto;

import java.time.LocalDateTime;

public record UserDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        Long defaultPaymentMethodId,
        LocalDateTime createdAt
) {
}
