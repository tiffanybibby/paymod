package com.tiffany.paymod.controllers;

import com.tiffany.paymod.model.Payment;
import com.tiffany.paymod.model.PaymentHistory;
import com.tiffany.paymod.model.PaymentStatus;
import com.tiffany.paymod.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<List<Payment>> getPayments() {
        return new ResponseEntity<>(paymentService.getAllPayments(), HttpStatus.OK);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<Payment> getPayment(@PathVariable Long paymentId) {
        return paymentService.getPayment(paymentId)
                .map(payment -> new ResponseEntity<>(payment, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(headers = "X-User-ID")
    public ResponseEntity<List<Payment>> getPaymentsByUser(@RequestHeader("X-User-ID") Long userId) {
        return new ResponseEntity<>(paymentService.listPaymentsByUser(userId), HttpStatus.OK);
    }

    @GetMapping(value = "/status/{paymentStatus}", headers = "X-User-ID")
    public ResponseEntity<List<Payment>> getAllPaymentsByUserAndStatus(@RequestHeader("X-User-ID") Long userId, @PathVariable String paymentStatus) {
        return new ResponseEntity<>(paymentService.listPaymentsByUserAndStatus(userId, PaymentStatus.valueOf(paymentStatus)), HttpStatus.OK);
    }

//    @PostMapping
//    public ResponseEntity<String> createPayment(@RequestHeader("X-User-ID") Long userId, @RequestBody Payment payment) {
//        if (!paymentService.createPayment(userId, payment)) {
//            return ResponseEntity.badRequest().body("Unable to create payment");
//        } else {
//            return ResponseEntity.status(HttpStatus.CREATED).build();
//        }
//    }

    @PostMapping
    public ResponseEntity<Payment> createPayment(
            @RequestHeader("X-User-ID") Long userId,
            @RequestBody java.util.Map<String, Object> payload
    ) {
        Long paymentMethodId = asLong(payload.get("paymentMethodId"));
        BigDecimal amount = asBigDecimal(payload.get("amount"));
        String currency = payload.get("currency") == null ? "USD" : payload.get("currency").toString();

        if (paymentMethodId == null || amount == null) return ResponseEntity.badRequest().build();

        Payment payment = paymentService.createAndProcess(userId, paymentMethodId, amount, currency);
        return new ResponseEntity<>(payment, HttpStatus.OK);
    }

    private Long asLong(Object value) {
        if (value == null) return null;
        if (value instanceof Number n) return n.longValue();
        return Long.parseLong(value.toString());
    }

    private BigDecimal asBigDecimal(Object value) {
        if (value == null) return null;
        if (value instanceof BigDecimal decimal) return decimal;
        return new BigDecimal(value.toString());
    }

    @PutMapping("/{id}/process")
    public ResponseEntity<Payment> process(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.process(id));
    }

    @PutMapping("/{paymentId}/capture")
    public ResponseEntity<String> capturePayment(@RequestHeader("X-User-ID") Long userId, @PathVariable Long paymentId) {
        boolean paymentComplete = paymentService.capturePayment(userId, paymentId);
        return paymentComplete ? ResponseEntity.ok().body("Payment successful") : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PutMapping("/{paymentId}/fail")
    public ResponseEntity<String> failPayment(@RequestHeader("X-User-ID") Long userId, @PathVariable Long paymentId) {
        boolean paymentComplete = paymentService.failPayment(userId, paymentId);
        return paymentComplete ? ResponseEntity.ok().body("Payment failed") : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @GetMapping("/{paymentId}/history")
    public ResponseEntity<List<PaymentHistory>> getPaymentHistory(@PathVariable Long paymentId) {
        return new ResponseEntity<>(paymentService.findByPaymentIdOrderByOccurredAtAsc(paymentId), HttpStatus.OK);
    }
}
