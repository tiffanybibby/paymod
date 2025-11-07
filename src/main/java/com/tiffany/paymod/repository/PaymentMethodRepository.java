package com.tiffany.paymod.repository;

import com.tiffany.paymod.model.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod,Long> {

    List<PaymentMethod> findByUserIdAndDeletedAtIsNullOrderByIsDefaultDescIdAsc(Long userId);

    Optional<PaymentMethod> findByIdAndUserIdAndDeletedAtIsNull(Long id, Long userId);
}
