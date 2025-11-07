package com.tiffany.paymod.utility;

import com.tiffany.paymod.dto.*;
import com.tiffany.paymod.model.*;

public final class ApiMapperUtil {

    private ApiMapperUtil() {
    }

    public static UserDto toUserDto(User user) {
        if (user == null) return null;
        Long defaultPaymentMethodId = (user.getDefaultPaymentMethod() != null)
                ? user.getDefaultPaymentMethod().getId()
                : null;
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                defaultPaymentMethodId,
                user.getCreatedAt()
        );
    }

    public static PaymentMethodDto toPaymentMethodDto(PaymentMethod paymentMethod) {
        if (paymentMethod == null) return null;
        return new PaymentMethodDto(
                paymentMethod.getId(),
                paymentMethod.getLabel(),
                paymentMethod.getBrand(),
                paymentMethod.getLast4(),
                paymentMethod.getExpMonth(),
                paymentMethod.getExpYear(),
                paymentMethod.isDefault(),
                paymentMethod.getStatus(),
                paymentMethod.getCreatedAt()
        );
    }

    public static PaymentDto toPaymentDto(Payment payment) {
        if (payment == null) return null;
        Long userId = payment.getUser() != null ? payment.getUser().getId() : null;
        Long paymentId = payment.getPaymentMethod() != null ? payment.getPaymentMethod().getId() : null;
        return new PaymentDto(
                payment.getId(),
                userId,
                paymentId,
                payment.getAmount(),
                payment.getCurrency(),
                payment.getPaymentStatus(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }

    public static PaymentHistoryDto toPaymentHistoryDto(PaymentHistory paymentHistory) {
        if (paymentHistory == null) return null;

        return new PaymentHistoryDto(
                paymentHistory.getId(),
                paymentHistory.getPayment().getId(),
                paymentHistory.getOldStatus(),
                paymentHistory.getNewStatus(),
                paymentHistory.getAmount(),
                paymentHistory.getCurrency(),
                paymentHistory.getEventType(),
                paymentHistory.getOccurredAt()
        );
    }
}
