package com.tiffany.paymod.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePaymentMethodRequest(
        @NotBlank
        String brand,

        @NotBlank
        String last4,

        @NotNull
        Integer expMonth,

        @NotNull
        Integer expYear,

        String label
) {
}
