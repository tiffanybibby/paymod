package com.tiffany.paymod.service;

import com.tiffany.paymod.model.PaymentMethod;

import java.util.List;
import java.util.Map;

public interface PaymentMethodService {

    PaymentMethod add(Long userId, Map<String, Object> payload);

    List<PaymentMethod> list(Long userId);

    boolean patch(Long userId, Long paymentMethodId, Map<String, Object> payload);

    void softDelete(Long userId, Long pmId);
}
