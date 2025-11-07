package com.tiffany.paymod.service.impl;

import com.tiffany.paymod.domain.PaymentCreatedDomainEvent;
import com.tiffany.paymod.domain.PaymentStatusChangedDomainEvent;
import com.tiffany.paymod.dto.PaymentDto;
import com.tiffany.paymod.dto.PaymentHistoryDto;
import com.tiffany.paymod.gateway.PaymentGateway;
import com.tiffany.paymod.utility.ApiMapperUtil;
import com.tiffany.paymod.model.*;
import com.tiffany.paymod.repository.PaymentHistoryRepository;
import com.tiffany.paymod.repository.PaymentMethodRepository;
import com.tiffany.paymod.repository.PaymentRepository;
import com.tiffany.paymod.repository.UserRepository;
import com.tiffany.paymod.service.PaymentService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher events;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentGateway gateway;


    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto> getAllPayments() {
        return paymentRepository.findAll().stream().map(ApiMapperUtil::toPaymentDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentDto> getPayment(Long paymentId) {
        return paymentRepository.findById(paymentId).map(ApiMapperUtil::toPaymentDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto> listPaymentsByUser(Long userId) {
        return paymentRepository.findPaymentsByUserId(userId).stream().map(ApiMapperUtil::toPaymentDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto> listPaymentsByUserAndStatus(Long userId, PaymentStatus paymentStatus) {
        return paymentRepository.findPaymentsByUserIdAndPaymentStatus(userId, paymentStatus).stream().map(ApiMapperUtil::toPaymentDto).toList();
    }

//    @Override
//    @Transactional
//    public boolean createPayment(Long userId, PaymentDto payment) {
//        return userRepository.findById(userId)
//                .map(existingUser -> {
//                    payment.setUser(existingUser);
//                    Payment saved = paymentRepository.save(payment);
//                    events.publishEvent(new PaymentCreatedDomainEvent(saved.getId()));
//                    return true;
//                }).orElse(false);
//    }

    @Override
    @Transactional
    public boolean capturePayment(Long userId, Long paymentId) {
        return paymentRepository.findById(paymentId)
                .map(existingPayment -> {
                    if (existingPayment.getUser() != null && userId.equals(existingPayment.getUser().getId())) {
                        PaymentStatus oldStatus = existingPayment.getPaymentStatus();
                        existingPayment.setPaymentStatus(PaymentStatus.SUCCESS);
                        paymentRepository.save(existingPayment);
                        events.publishEvent(new PaymentStatusChangedDomainEvent(
                                existingPayment.getId(), oldStatus, existingPayment.getPaymentStatus()
                        ));
                        return true;
                    }
                    return false;
                }).orElse(false);
    }

    @Override
    @Transactional
    public boolean failPayment(Long userId, Long paymentId) {
        return paymentRepository.findById(paymentId)
                .map(existingPayment -> {
                    if (existingPayment.getUser() != null && userId.equals(existingPayment.getUser().getId())) {
                        PaymentStatus oldStatus = existingPayment.getPaymentStatus();
                        existingPayment.setPaymentStatus(PaymentStatus.FAILED);
                        paymentRepository.save(existingPayment);
                        events.publishEvent(new PaymentStatusChangedDomainEvent(
                                existingPayment.getId(), oldStatus, existingPayment.getPaymentStatus()
                        ));
                        return true;
                    }
                    return false;
                }).orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentHistoryDto> findByPaymentIdOrderByOccurredAtAsc(Long paymentId) {
        return paymentHistoryRepository.findByPaymentIdOrderByOccurredAtAsc(paymentId).stream().map(ApiMapperUtil::toPaymentHistoryDto).toList();
    }

    @Transactional
    @Override
    public PaymentDto createAndProcess(Long userId, Long paymentMethodId, BigDecimal amount, String currency) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        PaymentMethod paymentMethod = paymentMethodRepository.findByIdAndUserIdAndDeletedAtIsNull(paymentMethodId, userId).orElseThrow(() -> new IllegalArgumentException("Payment method not found for user"));

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Invalid amount");

        amount = amount.setScale(2, RoundingMode.HALF_UP);

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setPaymentMethod(paymentMethod);
        payment.setAmount(amount);
        payment.setCurrency(currency != null ? currency : "USD");
        payment.setPaymentStatus(PaymentStatus.PENDING);

        payment = paymentRepository.save(payment);
        events.publishEvent(new PaymentCreatedDomainEvent(payment.getId()));

        PaymentGateway.CreateChargeResult result = gateway.charge(paymentMethod.getToken(), amount, payment.getCurrency());

        if (result.status() != PaymentStatus.PENDING) {
            PaymentStatus oldStatus = payment.getPaymentStatus();
            payment.setPaymentStatus(result.status());
            payment = paymentRepository.save(payment);

            events.publishEvent(new PaymentStatusChangedDomainEvent(
                    payment.getId(), oldStatus, payment.getPaymentStatus()
            ));
        }
        return ApiMapperUtil.toPaymentDto(payment);
    }

    @Transactional
    @Override
    public PaymentDto process(Long paymentId) {
        var payment = paymentRepository.findById(paymentId).orElseThrow();
        if (payment.getPaymentStatus() != PaymentStatus.PENDING)
            return ApiMapperUtil.toPaymentDto(payment);

        PaymentMethod paymentMethod = payment.getPaymentMethod();
        PaymentGateway.CreateChargeResult result = gateway.charge(paymentMethod.getToken(), payment.getAmount(), payment.getCurrency());

        PaymentStatus oldStatus = payment.getPaymentStatus();
        payment.setPaymentStatus(result.status());
        payment = paymentRepository.save(payment);

        if (oldStatus != result.status()) {
            events.publishEvent(new PaymentStatusChangedDomainEvent(payment.getId(), oldStatus, result.status()));
        }
        return ApiMapperUtil.toPaymentDto(payment);
    }
}
