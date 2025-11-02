package com.tiffany.paymod.repository;

import com.tiffany.paymod.model.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory,Long> {
    boolean existsByEventId(String eventId);
    List<PaymentHistory> findByPaymentIdOrderByOccurredAtAsc(Long paymentId);
}
