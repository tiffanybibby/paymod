package com.tiffany.paymod.repository;

import com.tiffany.paymod.model.Payment;
import com.tiffany.paymod.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long> {
    List<Payment> findPaymentsByUserId(Long user_id);

    List<Payment> findPaymentsByUserIdAndPaymentStatus(Long userId, PaymentStatus paymentStatus);
}
