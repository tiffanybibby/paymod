package com.tiffany.paymod.service;

import com.tiffany.paymod.dto.CreatePaymentMethodRequest;
import com.tiffany.paymod.dto.PaymentMethodDto;
import com.tiffany.paymod.dto.UpdatePaymentMethodRequest;
import com.tiffany.paymod.model.PaymentMethod;

import java.util.List;
import java.util.Map;

public interface PaymentMethodService {

    List<PaymentMethodDto> list(Long userId);

    PaymentMethodDto add(Long userId, CreatePaymentMethodRequest request);

    boolean patch(Long userId, Long paymentMethodId, UpdatePaymentMethodRequest request);

    void softDelete(Long userId, Long pmId);
}
