package com.tiffany.paymod.service.impl;

import com.tiffany.paymod.model.Payment;
import com.tiffany.paymod.model.PaymentStatus;
import com.tiffany.paymod.model.User;
import com.tiffany.paymod.service.PaymentService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final List<Payment> paymentList = new ArrayList<>();
    private final UserServiceImpl userServiceImpl;
    private Long nextId = 1L;

    public PaymentServiceImpl(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentList;
    }

    @Override
    public Optional<Payment> getPayment(Long paymentId) {
        return paymentList.stream().filter(payment -> payment.getId().equals(paymentId)).findFirst();
    }

    @Override
    public List<Payment> fetchPaymentsByUser(String userId) {
        return paymentList.stream().filter(payment -> payment.getUser() != null && Long.valueOf(userId).equals(payment.getUser().getId())).toList();
    }

    @Override
    public List<Payment> getAllPaymentsByUserAndStatus(String userId, PaymentStatus paymentStatus) {
    return paymentList.stream().filter(payment -> payment.getUser() != null && Long.valueOf(userId).equals(payment.getUser().getId())).toList().stream()
            .filter(payment -> payment.getPaymentStatus() == paymentStatus).toList();
    }

    @Override
    public boolean createPayment(String userId, Payment payment) {
        Optional<User> user = userServiceImpl.fetchUser(Long.valueOf(userId));
        if (user.isPresent()) {
            payment.setUser(user.get());
            payment.setId(nextId++);
            payment.setCreatedAt(LocalDateTime.now());
            payment.setUpdatedAt(LocalDateTime.now());
            paymentList.add(payment);
            return true;
        }
        return false;
    }

    @Override
    public boolean submitPayment(String userId, Payment payment, Long paymentId) {
        Optional<Payment> existingPaymentOpt = getPayment(paymentId);
        if (existingPaymentOpt.isPresent()) {
            Payment existingPayment = existingPaymentOpt.get();
            if (existingPayment.getUser() != null && Long.valueOf(userId).equals(existingPayment.getUser().getId())) {
                existingPayment.setAmount(payment.getAmount());
                existingPayment.setPaymentStatus(PaymentStatus.SUCCESS);
                existingPayment.setUpdatedAt(LocalDateTime.now());
                return true;
            }
        }
        return false;
    }
}
