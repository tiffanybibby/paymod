package com.tiffany.paymod.config;

import com.tiffany.paymod.model.*;
import com.tiffany.paymod.repository.PaymentMethodRepository;
import com.tiffany.paymod.repository.PaymentRepository;
import com.tiffany.paymod.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;

// Configuration class to seed development data for user + pending payments in dev/local
@Configuration
@Profile({"dev","default"})
public class DevDataConfig {

    @Bean
    CommandLineRunner seedPendingPayment(UserRepository users,
                                         PaymentMethodRepository paymentMethodRepository,
                                         PaymentRepository paymentRepository) {
        return args -> {

            User user = users.findByEmail("tiffany@example.com").orElseGet(() -> {
                User newUser = new User();
                newUser.setEmail("tiffany@example.com");
                newUser.setFirstName("Tiffany");
                newUser.setLastName("Bibby");
                return users.save(newUser);
            });

            PaymentMethod paymentMethod1 = paymentMethodRepository.findByUserIdAndDeletedAtIsNullOrderByIsDefaultDescIdAsc(user.getId())
                    .stream()
                    .filter(pm -> "token_demo_R".equals(pm.getToken()))
                    .findFirst()
                    .orElseGet(() -> {
                        PaymentMethod newPaymentMethod = PaymentMethod.builder()
                                .user(user)
                                .provider("MOCK")
                                .token("token_demo_R")
                                .brand("VISA")
                                .last4("4242")
                                .expMonth(12)
                                .expYear(2030)
                                .label("Personal Visa")
                                .status(PaymentMethodStatus.ACTIVE)
                                .build();
                        newPaymentMethod.setDefault(true);
                        return paymentMethodRepository.save(newPaymentMethod);
                    });

            PaymentMethod paymentMethod2 = paymentMethodRepository.findByUserIdAndDeletedAtIsNullOrderByIsDefaultDescIdAsc(user.getId())
                    .stream()
                    .filter(pm -> "token_demo_F".equals(pm.getToken()))
                    .findFirst()
                    .orElseGet(() -> {
                        PaymentMethod additional = PaymentMethod.builder()
                                .user(user)
                                .provider("MOCK")
                                .token("token_demo_F")
                                .brand("MASTERCARD")
                                .last4("5505")
                                .expMonth(11)
                                .expYear(2031)
                                .label("Business Master")
                                .status(PaymentMethodStatus.ACTIVE)
                                .build();
                        return paymentMethodRepository.save(additional);
                    });

            boolean alreadySeededWithR = paymentRepository.existsByPaymentStatusAndPaymentMethodToken(PaymentStatus.PENDING, "token_demo_R");

            if (!alreadySeededWithR) {
                Payment payment = new Payment();
                payment.setUser(user);
                payment.setPaymentMethod(paymentMethod1);
                payment.setAmount(new BigDecimal("49.99"));
                payment.setPaymentStatus(PaymentStatus.PENDING);
                paymentRepository.save(payment);
            }

            boolean alreadySeededWithF = paymentRepository.existsByPaymentStatusAndPaymentMethodToken(PaymentStatus.PENDING, "token_demo_F");

            if (!alreadySeededWithF) {
                Payment payment = new Payment();
                payment.setUser(user);
                payment.setPaymentMethod(paymentMethod2);
                payment.setAmount(new BigDecimal("18.50"));
                payment.setPaymentStatus(PaymentStatus.PENDING);
                paymentRepository.save(payment);
            }
        };
    }
}
