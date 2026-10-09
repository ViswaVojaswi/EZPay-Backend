package com.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.backend.entity.Account;
import com.backend.entity.Payment;
import com.backend.exception.PaymentException;
import com.backend.repository.AccountRepository;
import com.backend.repository.PaymentRepository;

@SpringBootTest
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeEach
    void setup() {

        paymentRepository.deleteAll();
        accountRepository.deleteAll();

        Account account = new Account();

        account.setUserId(101L);
        account.setAccountNumber("123456789012");
        account.setBalance(new BigDecimal("10000"));

        accountRepository.save(account);
    }

    @Test
    void testUpiPaymentSuccess() {

        Payment payment = new Payment();

        payment.setUserId(101L);
        payment.setSenderUpiId("user@upi");
        payment.setReceiverUpiId("receiver@upi");
        payment.setAmount(new BigDecimal("500"));
        payment.setNote("Test payment");

        Payment result =
                paymentService.makeUpiPayment(payment);

        assertNotNull(result.getPaymentId());
        assertEquals("UPI", result.getPaymentType());
        assertEquals("SUCCESS", result.getStatus());

        Account account =
                accountRepository.findByUserId(101L).orElseThrow();

        assertEquals(
                new BigDecimal("9500"),
                account.getBalance());
    }

    @Test
    void testBankTransferSuccess() {

        Payment payment = new Payment();

        payment.setUserId(101L);
        payment.setAccountNumber("987654321012");
        payment.setIfscCode("SBIN0001234");
        payment.setAmount(new BigDecimal("1000"));
        payment.setPurpose("House Rent");

        Payment result =
                paymentService.makeBankTransfer(payment);

        assertNotNull(result.getPaymentId());
        assertEquals("BANK_TRANSFER", result.getPaymentType());
        assertEquals("SUCCESS", result.getStatus());

        Account account =
                accountRepository.findByUserId(101L).orElseThrow();

        assertEquals(
                new BigDecimal("9000"),
                account.getBalance());
    }

    @Test
    void testInvalidUpiId() {

        Payment payment = new Payment();

        payment.setUserId(101L);
        payment.setReceiverUpiId("invalidupi");
        payment.setAmount(new BigDecimal("500"));

        PaymentException exception =
                assertThrows(
                        PaymentException.class,
                        () -> paymentService.makeUpiPayment(payment));

        assertEquals(
                "Invalid UPI ID.Please enter a valid upi ID",
                exception.getMessage());
    }

    @Test
    void testInsufficientBalance() {

        Payment payment = new Payment();

        payment.setUserId(101L);
        payment.setReceiverUpiId("receiver@upi");
        payment.setAmount(new BigDecimal("15000"));

        PaymentException exception =
                assertThrows(
                        PaymentException.class,
                        () -> paymentService.makeUpiPayment(payment));

        assertEquals(
                "Insufficient funds.Please enter a lower amount",
                exception.getMessage());
    }

    @Test
    void testInvalidBankDetails() {

        Payment payment = new Payment();

        payment.setUserId(101L);
        payment.setAccountNumber("123");
        payment.setIfscCode("SBIN0001234");
        payment.setAmount(new BigDecimal("500"));

        PaymentException exception =
                assertThrows(
                        PaymentException.class,
                        () -> paymentService.makeBankTransfer(payment));

        assertEquals(
                "Invalid account number",
                exception.getMessage());
    }

    @Test
    void testPaymentHistory() {

        Payment payment = new Payment();

        payment.setUserId(101L);
        payment.setReceiverUpiId("receiver@upi");
        payment.setAmount(new BigDecimal("500"));

        paymentService.makeUpiPayment(payment);

        var payments =
                paymentService.getPaymentsByUserId(101L);

        assertNotNull(payments);
        assertEquals(1, payments.size());
    }
}