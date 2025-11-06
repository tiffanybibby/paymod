package com.tiffany.paymod.gateway;

import com.tiffany.paymod.model.PaymentStatus;
import java.math.BigDecimal;

public interface PaymentGateway {

    CreateChargeResult charge(String token, BigDecimal amount, String currency);

    record CreateChargeResult(
            String providerRef,
            PaymentStatus status,
            String errorCode,
            String errorMessage
    ) {}
}
