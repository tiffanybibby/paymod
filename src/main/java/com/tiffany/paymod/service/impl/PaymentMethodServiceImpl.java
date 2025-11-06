package com.tiffany.paymod.service.impl;

import com.tiffany.paymod.model.PaymentMethod;
import com.tiffany.paymod.model.PaymentMethodStatus;
import com.tiffany.paymod.model.User;
import com.tiffany.paymod.repository.PaymentMethodRepository;
import com.tiffany.paymod.repository.UserRepository;
import com.tiffany.paymod.service.PaymentMethodService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentMethodServiceImpl implements PaymentMethodService {
    private final PaymentMethodRepository paymentMethodRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public PaymentMethod add(Long userId, Map<String, Object> payload) {
        User user = userRepository.getReferenceById(userId);
        PaymentMethod paymentMethod = PaymentMethod.builder()
                .user(user)
                .provider("MOCK")
                .token(mint())
                .brand(asString(payload.get("brand")))
                .last4(asString(payload.get("last4")))
                .expMonth(asInteger(payload.get("expMonth")))
                .expYear(asInteger(payload.get("expYear")))
                .label(asString(payload.get("label")))
                .status(PaymentMethodStatus.ACTIVE)
                .build();
        boolean isFirstPaymentMethod = paymentMethodRepository.findByUserIdAndDeletedAtIsNullOrderByIsDefaultDescIdAsc(userId).isEmpty();
        if (isFirstPaymentMethod) {
            paymentMethod.setDefault(true);
            user.setDefaultPaymentMethod(paymentMethod);
        }
        return paymentMethodRepository.save(paymentMethod);
    }

    @Override
    public List<PaymentMethod> list(Long userId) {
        return paymentMethodRepository.findByUserIdAndDeletedAtIsNullOrderByIsDefaultDescIdAsc(userId);
    }

    @Override
    @Transactional
    public boolean patch(Long userId, Long paymentMethodId, Map<String, Object> payload) {
        PaymentMethod paymentMethod = paymentMethodRepository.findByIdAndUserIdAndDeletedAtIsNull(paymentMethodId, userId).orElse(null);
        if (paymentMethod == null) return false;
        if (payload.containsKey("label")) paymentMethod.setLabel(asString(payload.get("label").toString()));
        if (Boolean.TRUE.equals(payload.get("isDefault"))) {
            List<PaymentMethod> allPaymentMethods = paymentMethodRepository.findByUserIdAndDeletedAtIsNullOrderByIsDefaultDescIdAsc(userId);
            for (PaymentMethod existingPaymentMethod : allPaymentMethods) existingPaymentMethod.setDefault(existingPaymentMethod.getId().equals(paymentMethodId));
        }
        if (payload.containsKey("status")) {
            String status = asString(payload.get("status"));
            if (status != null) paymentMethod.setStatus(PaymentMethodStatus.valueOf(status));
        }
        return true;
    }

    @Override
    @Transactional
    public void softDelete(Long userId, Long paymentMethodId) {
        PaymentMethod paymentMethod = paymentMethodRepository.findByIdAndUserIdAndDeletedAtIsNull(paymentMethodId, userId).orElseThrow();
        paymentMethod.setDeletedAt(LocalDateTime.now());
        paymentMethod.setDefault(false);
        paymentMethod.setStatus(PaymentMethodStatus.INACTIVE);
    }

    private static String mint() {
        return "tok_" + UUID.randomUUID().toString().replace("-", "");
    }

    private static String asString(Object value) {
        return value == null ? null : value.toString().trim();
    }

    private static Integer asInteger(Object value) {
        if (value == null) return null;
        if (value instanceof Number number) return number.intValue();
        return Integer.parseInt(value.toString());
    }
}
