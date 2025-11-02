package com.tiffany.paymod.service.impl;

import com.tiffany.paymod.model.Payment;
import com.tiffany.paymod.model.PaymentStatus;
import com.tiffany.paymod.repository.PaymentRepository;
import com.tiffany.paymod.repository.UserRepository;
import com.tiffany.paymod.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

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
    public boolean createPayment(Long userId, Payment payment) {
        return userRepository.findById(userId)
                .map(existingUser -> {
                    payment.setUser(existingUser);
                    paymentRepository.save(payment);
                    return true;
                }).orElse(false);
    }

    @Override
    public boolean capturePayment(Long userId, Long paymentId) {
        return paymentRepository.findById(paymentId)
                .map(existingPayment -> {
                    if (existingPayment.getUser() != null && userId.equals(existingPayment.getUser().getId())) {
                        existingPayment.setPaymentStatus(PaymentStatus.SUCCESS);
                        paymentRepository.save(existingPayment);
                        return true;
                    }
                    return false;
                }).orElse(false);
    }

    @Override
    public boolean failPayment(Long userId, Long paymentId) {
        return paymentRepository.findById(paymentId)
                .map(existingPayment -> {
                    if (existingPayment.getUser() != null && userId.equals(existingPayment.getUser().getId())) {
                        existingPayment.setPaymentStatus(PaymentStatus.FAILED);
                        paymentRepository.save(existingPayment);
                        return true;
                    }
                    return false;
                }).orElse(false);
    }
}
