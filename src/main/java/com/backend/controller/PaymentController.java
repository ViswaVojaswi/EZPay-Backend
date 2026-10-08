package com.backend.controller;

import java.util.List;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.backend.entity.Payment;
import com.backend.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // UPI Payment
    @PostMapping("/upi")
    public ResponseEntity<Payment> makeUpiPayment(
            @RequestBody Payment payment) {

        Payment savedPayment =
                paymentService.makeUpiPayment(payment);

        return new ResponseEntity<>(
                savedPayment,
                HttpStatus.CREATED);
    }

    // Bank Transfer
    @PostMapping("/bank-transfer")
    public ResponseEntity<Payment> makeBankTransfer(
            @RequestBody Payment payment) {

        Payment savedPayment =
                paymentService.makeBankTransfer(payment);

        return new ResponseEntity<>(
                savedPayment,
                HttpStatus.CREATED);
    }

    // Payment History
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Payment>> getUserPayments(
            @PathVariable Long userId) {

        List<Payment> payments =
                paymentService.getPaymentsByUserId(userId);

        return ResponseEntity.ok(payments);
    }

    // Get Payment By ID
    @GetMapping("/{paymentId}")
    public ResponseEntity<Payment> getPayment(
            @PathVariable Long paymentId) {

        Payment payment =
                paymentService.getPaymentById(paymentId);

        return ResponseEntity.ok(payment);
    }
}