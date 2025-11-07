package com.tiffany.paymod.service;

import com.tiffany.paymod.dto.PaymentDto;
import com.tiffany.paymod.dto.PaymentHistoryDto;
import com.tiffany.paymod.model.PaymentStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PaymentService {


    List<PaymentDto> getAllPayments();

    Optional<PaymentDto> getPayment(Long paymentId);

    List<PaymentDto> listPaymentsByUser(Long userId);

    List<PaymentDto> listPaymentsByUserAndStatus(Long userId, PaymentStatus paymentStatus);

//    boolean createPayment(Long userId, Payment payment);

    boolean capturePayment(Long userId, Long paymentId);

    boolean failPayment(Long userId, Long paymentId);

    List<PaymentHistoryDto> findByPaymentIdOrderByOccurredAtAsc(Long paymentId);

    PaymentDto createAndProcess(Long userId, Long paymentMethodId, BigDecimal amount, String currency);

    PaymentDto process(Long paymentId);
}
