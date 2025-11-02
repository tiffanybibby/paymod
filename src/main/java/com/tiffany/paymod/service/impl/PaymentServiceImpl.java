package com.tiffany.paymod.service.impl;

import com.tiffany.paymod.domain.PaymentCreatedDomainEvent;
import com.tiffany.paymod.domain.PaymentStatusChangedDomainEvent;
import com.tiffany.paymod.model.Payment;
import com.tiffany.paymod.model.PaymentHistory;
import com.tiffany.paymod.model.PaymentStatus;
import com.tiffany.paymod.repository.PaymentHistoryRepository;
import com.tiffany.paymod.repository.PaymentRepository;
import com.tiffany.paymod.repository.UserRepository;
import com.tiffany.paymod.service.PaymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher events;
    private final PaymentHistoryRepository paymentHistoryRepository;

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public Optional<Payment> getPayment(Long paymentId) {
        return paymentRepository.findById(paymentId);
    }

    @Override
    public List<Payment> listPaymentsByUser(Long userId) {
        return paymentRepository.findPaymentsByUserId(userId);
    }

    @Override
    public List<Payment> listPaymentsByUserAndStatus(Long userId, PaymentStatus paymentStatus) {
        return paymentRepository.findPaymentsByUserIdAndPaymentStatus(userId, paymentStatus);
    }

    @Override
    @Transactional
    public boolean createPayment(Long userId, Payment payment) {
        return userRepository.findById(userId)
                .map(existingUser -> {
                    payment.setUser(existingUser);
                    Payment saved = paymentRepository.save(payment);
                    events.publishEvent(new PaymentCreatedDomainEvent(saved.getId()));
                    return true;
                }).orElse(false);
    }

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
    public List<PaymentHistory> findByPaymentIdOrderByOccurredAtAsc(Long paymentId) {
        return paymentHistoryRepository.findByPaymentIdOrderByOccurredAtAsc(paymentId);
    }
}
