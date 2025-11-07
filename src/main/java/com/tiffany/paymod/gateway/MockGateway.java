package com.tiffany.paymod.gateway;

import com.tiffany.paymod.model.PaymentStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class MockGateway implements PaymentGateway {

    @Override
    public CreateChargeResult charge(String token, BigDecimal amount, String currency) {
        String ref = "pay_" + UUID.randomUUID();

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return new CreateChargeResult(ref, PaymentStatus.FAILED, "amount_invalid", "Amount must be > 0");
        }
        if (token != null && token.endsWith("F")) {
            return new CreateChargeResult(ref, PaymentStatus.FAILED, "card_declined", "Mock card decline");
        }
        if (token != null && token.endsWith("R")) {
            return new CreateChargeResult(ref, PaymentStatus.PENDING, null, null);
        }
        return new CreateChargeResult(ref, PaymentStatus.SUCCESS, null, null);
    }
}
