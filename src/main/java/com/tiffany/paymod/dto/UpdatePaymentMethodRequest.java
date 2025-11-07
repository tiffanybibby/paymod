package com.tiffany.paymod.dto;

public record UpdatePaymentMethodRequest(
        String label,
        Boolean isDefault
) {
}
