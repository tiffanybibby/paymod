package com.tiffany.paymod.controllers;

import com.tiffany.paymod.dto.CreatePaymentMethodRequest;
import com.tiffany.paymod.dto.PaymentMethodDto;
import com.tiffany.paymod.dto.UpdatePaymentMethodRequest;
import com.tiffany.paymod.model.PaymentMethod;
import com.tiffany.paymod.service.PaymentMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wallet")
public class PaymentMethodController {

    private final PaymentMethodService service;

    @GetMapping("/payment-methods")
    public List<PaymentMethodDto> list(@RequestHeader("X-User-ID") Long userId) {
        return service.list(userId);
    }

    @PostMapping("/payment-methods")
    public ResponseEntity<PaymentMethodDto> add(
            @RequestHeader("X-User-ID") Long userId,
            @RequestBody CreatePaymentMethodRequest request
            ) {
        return ResponseEntity.ok(service.add(userId, request));
    }

    @PatchMapping("/payment-methods/{id}")
    public ResponseEntity<Void> patch(
            @RequestHeader("X-User-ID") Long userId,
            @PathVariable Long id,
            @RequestBody UpdatePaymentMethodRequest request
    ) {
        boolean ok = service.patch(userId, id, request);
        return ok ? ResponseEntity.noContent().build() : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/payment-methods/{id}")
    public ResponseEntity<Void> delete(
            @RequestHeader("X-User-ID") Long userId,
            @PathVariable Long id
    ) {
        service.softDelete(userId, id);
        return ResponseEntity.noContent().build();
    }
}
