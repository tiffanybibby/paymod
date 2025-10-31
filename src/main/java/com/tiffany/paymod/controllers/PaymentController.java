package com.tiffany.paymod.controllers;

import com.tiffany.paymod.model.Payment;
import com.tiffany.paymod.model.PaymentStatus;
import com.tiffany.paymod.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/payment")
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
    public ResponseEntity<List<Payment>> getPaymentsByUser(@RequestHeader("X-User-ID") String userId) {
        return new ResponseEntity<>(paymentService.fetchPaymentsByUser(userId),HttpStatus.OK);
    }

    @GetMapping(value = "/status/{paymentStatus}", headers = "X-User-ID")
    public ResponseEntity<List<Payment>> getAllPaymentsByUserAndStatus(@RequestHeader("X-User-ID") String userId, @PathVariable String paymentStatus) {
        return new ResponseEntity<>(paymentService.getAllPaymentsByUserAndStatus(userId, PaymentStatus.valueOf(paymentStatus)),HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> createPaymentRequest(@RequestHeader("X-User-ID") String userId, @RequestBody Payment payment) {
        if (!paymentService.createPayment(userId, payment)) {
            return ResponseEntity.badRequest().body("Unable to create payment");
        } else{
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
    }

    @PutMapping("/{paymentId}")
    public ResponseEntity<String> submitPayment(@RequestHeader("X-User-ID") String userId, @PathVariable Long paymentId, @RequestBody Payment payment){
        boolean paymentComplete = paymentService.submitPayment(userId, payment, paymentId);
        return paymentComplete ? ResponseEntity.badRequest().body("Payment successful") : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
