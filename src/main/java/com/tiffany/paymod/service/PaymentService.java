package com.tiffany.paymod.service;

import com.tiffany.paymod.model.Payment;
import com.tiffany.paymod.model.PaymentStatus;

import java.util.List;
import java.util.Optional;

public interface PaymentService {


    List<Payment> getAllPaymentsByUserAndStatus(String userId, PaymentStatus paymentStatus);

    boolean createPayment(String userId, Payment payment);

    boolean submitPayment(String userId, Payment payment, Long paymentId);

    Optional<Payment> getPayment(Long paymentId);

    List<Payment> fetchPaymentsByUser(String userId);

    List<Payment> getAllPayments();
}
