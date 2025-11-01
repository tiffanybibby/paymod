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
    public List<Payment> fetchPaymentsByUser(String userId) {
        return paymentRepository.findPaymentsByUserId(Long.valueOf(userId));
    }

    @Override
    public List<Payment> getAllPaymentsByUserAndStatus(String userId, PaymentStatus paymentStatus) {
    return paymentRepository.findPaymentsByUserIdAndPaymentStatus(Long.valueOf(userId),paymentStatus);
    }

    @Override
    public boolean createPayment(String userId, Payment payment) {
        return userRepository.findById(Long.valueOf(userId))
                .map(existingUser -> {
                    payment.setUser(existingUser);
                    paymentRepository.save(payment);
                    return true;
                }).orElse(false);
    }

    @Override
    public boolean submitPayment(String userId, Payment payment, Long paymentId) {
        Optional<Payment> existingPaymentOpt = getPayment(paymentId);
        if (existingPaymentOpt.isPresent()) {
            Payment existingPayment = existingPaymentOpt.get();
            if (existingPayment.getUser() != null && Long.valueOf(userId).equals(existingPayment.getUser().getId())) {
                existingPayment.setAmount(payment.getAmount());
                existingPayment.setPaymentStatus(PaymentStatus.SUCCESS);
                paymentRepository.save(existingPayment);
                return true;
            }
        }
        return false;
    }
}
