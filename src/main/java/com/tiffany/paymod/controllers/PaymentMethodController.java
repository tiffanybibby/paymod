package com.tiffany.paymod.controllers;

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

    @PostMapping("/payment-methods")
    public ResponseEntity<PaymentMethod> add(
            @RequestHeader("X-User-ID") Long userId,
            @RequestBody Map<String, Object> payload
    ) {
        return ResponseEntity.ok(service.add(userId, payload));
    }

    @GetMapping("/payment-methods")
    public List<PaymentMethod> list(@RequestHeader("X-User-ID") Long userId) {
        return service.list(userId);
    }

    @PatchMapping("/payment-methods/{id}")
    public ResponseEntity<Void> patch(
            @RequestHeader("X-User-ID") Long userId,
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload
    ) {
        boolean ok = service.patch(userId, id, payload);
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
