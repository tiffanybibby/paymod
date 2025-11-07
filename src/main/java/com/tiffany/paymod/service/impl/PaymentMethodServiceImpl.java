package com.tiffany.paymod.service.impl;

import com.tiffany.paymod.dto.CreatePaymentMethodRequest;
import com.tiffany.paymod.dto.PaymentMethodDto;
import com.tiffany.paymod.dto.UpdatePaymentMethodRequest;
import com.tiffany.paymod.utility.ApiMapperUtil;
import com.tiffany.paymod.model.PaymentMethod;
import com.tiffany.paymod.model.PaymentMethodStatus;
import com.tiffany.paymod.model.User;
import com.tiffany.paymod.repository.PaymentMethodRepository;
import com.tiffany.paymod.repository.UserRepository;
import com.tiffany.paymod.service.PaymentMethodService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentMethodServiceImpl implements PaymentMethodService {
    private final PaymentMethodRepository paymentMethodRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PaymentMethodDto> list(Long userId) {
        return paymentMethodRepository.findByUserIdAndDeletedAtIsNullOrderByIsDefaultDescIdAsc(userId).stream().map(ApiMapperUtil::toPaymentMethodDto).toList();
    }

    @Override
    @Transactional
    public PaymentMethodDto add(Long userId, CreatePaymentMethodRequest request) {
        User user = userRepository.getReferenceById(userId);
        PaymentMethod paymentMethod = PaymentMethod.builder()
                .user(user)
                .provider("MOCK")
                .token(mint())
                .brand(request.brand())
                .last4(request.last4())
                .expMonth(request.expMonth())
                .expYear(request.expYear())
                .label(request.label() != null ? request.label() : request.brand() + " " + request.last4())
                .status(PaymentMethodStatus.ACTIVE)
                .build();
        boolean isFirstPaymentMethod = paymentMethodRepository.findByUserIdAndDeletedAtIsNullOrderByIsDefaultDescIdAsc(userId).isEmpty();
        if (isFirstPaymentMethod) {
            paymentMethod.setDefault(true);
            user.setDefaultPaymentMethod(paymentMethod);
        }
        return ApiMapperUtil.toPaymentMethodDto(paymentMethodRepository.save(paymentMethod));
    }

    @Override
    @Transactional
    public boolean patch(Long userId, Long paymentMethodId, UpdatePaymentMethodRequest request) {
        PaymentMethod paymentMethod = paymentMethodRepository.findByIdAndUserIdAndDeletedAtIsNull(paymentMethodId, userId).orElse(null);
        if (paymentMethod == null) return false;
        if (request.label() != null) {
            paymentMethod.setLabel(request.label());
        }
        if (request.isDefault() != null && request.isDefault()) {
            List<PaymentMethod> allPaymentMethods = paymentMethodRepository.findByUserIdAndDeletedAtIsNullOrderByIsDefaultDescIdAsc(userId);
            for (PaymentMethod existingPaymentMethod : allPaymentMethods)
                existingPaymentMethod.setDefault(existingPaymentMethod.getId().equals(paymentMethodId));
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
        User user = userRepository.getReferenceById(userId);
        if (user.getDefaultPaymentMethod().getId().equals(paymentMethodId)) {
            List<PaymentMethod> remainingPaymentMethods = paymentMethodRepository.findByUserIdAndDeletedAtIsNullOrderByIsDefaultDescIdAsc(userId);
            if (!remainingPaymentMethods.isEmpty()) {
                PaymentMethod newDefault = remainingPaymentMethods.getFirst();
                newDefault.setDefault(true);
                user.setDefaultPaymentMethod(newDefault);
            } else user.setDefaultPaymentMethod(null);
        }
    }

    private static String mint() {
        return "tok_" + UUID.randomUUID().toString().replace("-", "");
    }
}
