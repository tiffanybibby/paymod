package com.tiffany.paymod.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Payment {
    private Long id;
    private User user;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer version = 1;
}
