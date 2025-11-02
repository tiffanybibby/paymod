package com.tiffany.paymod.service;

import com.tiffany.paymod.model.Payment;
import com.tiffany.paymod.model.PaymentHistory;
import com.tiffany.paymod.model.PaymentStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface PaymentService {


    List<Payment> getAllPayments();

    Optional<Payment> getPayment(Long paymentId);

    List<Payment> listPaymentsByUser(Long userId);

    List<Payment> listPaymentsByUserAndStatus(Long userId, PaymentStatus paymentStatus);

    boolean createPayment(Long userId, Payment payment);

    boolean capturePayment(Long userId, Long paymentId);

    boolean failPayment(Long userId, Long paymentId);

    List<PaymentHistory> findByPaymentIdOrderByOccurredAtAsc(Long paymentId);
}
