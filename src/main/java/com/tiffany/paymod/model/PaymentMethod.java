package com.tiffany.paymod.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "payment_method",
        indexes = { @Index(name = "idx_pm_user", columnList = "user_id") })
public class PaymentMethod {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 20)
    private String provider = "MOCK";

    //Token representing card data (instead of actual card details e.g. PAN/CVV)
    @Column(nullable = false, length = 64, unique = true)
    private String token;

    @Column(length = 32)
    private String brand;  // e.g. VISA/MC/AMEX/DISCOVER

    @Column(length = 4)
    private String last4;

    private Integer expMonth;

    private Integer expYear;

    @Column(length = 64)
    private String label;

    @Column(name = "is_default")
    private boolean isDefault;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private PaymentMethodStatus status = PaymentMethodStatus.ACTIVE;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime deletedAt;

    @Version
    private Integer version;
}
