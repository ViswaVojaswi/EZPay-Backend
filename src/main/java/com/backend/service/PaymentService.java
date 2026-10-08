package com.backend.service;

import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.entity.Account;
import com.backend.entity.Payment;
import com.backend.exception.PaymentException;
import com.backend.repository.AccountRepository;
import com.backend.repository.PaymentRepository;

@Service
public class PaymentService {

    private static final Logger logger =
            LogManager.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final AccountRepository accountRepository;

    public PaymentService(PaymentRepository paymentRepository,
                           AccountRepository accountRepository) {

        this.paymentRepository = paymentRepository;
        this.accountRepository = accountRepository;
    }

    // UPI Payment
    @Transactional
    public Payment makeUpiPayment(Payment payment) {

        logger.info("UPI payment request received for userId: {}",
                payment.getUserId());

        validateUpiPayment(payment);

        Account account = accountRepository
                .findByUserId(payment.getUserId())
                .orElseThrow(() ->
                        new PaymentException("Account not found"));

        checkBalance(account, payment.getAmount());

        account.setBalance(
                account.getBalance()
                        .subtract(payment.getAmount())
        );

        accountRepository.save(account);

        payment.setPaymentType("UPI");
        payment.setStatus("SUCCESS");
        payment.setPaymentDate(LocalDateTime.now());

        Payment savedPayment =
                paymentRepository.save(payment);

        logger.info("UPI payment successful. Payment ID: {}",
                savedPayment.getPaymentId());

        return savedPayment;
    }

    // Bank Transfer
    @Transactional
    public Payment makeBankTransfer(Payment payment) {

        logger.info("Bank transfer request received for userId: {}",
                payment.getUserId());

        validateBankTransfer(payment);

        Account account = accountRepository
                .findByUserId(payment.getUserId())
                .orElseThrow(() ->
                        new PaymentException("Account not found"));

        checkBalance(account, payment.getAmount());

        account.setBalance(
                account.getBalance()
                        .subtract(payment.getAmount())
        );

        accountRepository.save(account);

        payment.setPaymentType("BANK_TRANSFER");
        payment.setStatus("SUCCESS");
        payment.setPaymentDate(LocalDateTime.now());

        Payment savedPayment =
                paymentRepository.save(payment);

        logger.info("Bank transfer successful. Payment ID: {}",
                savedPayment.getPaymentId());

        return savedPayment;
    }

    // Validate UPI Payment
    private void validateUpiPayment(Payment payment) {

        if (payment.getUserId() == null) {
            throw new PaymentException("User ID is required");
        }

        if (payment.getReceiverUpiId() == null ||
                payment.getReceiverUpiId().isBlank()) {

            throw new PaymentException(
                    "Receiver UPI ID is required");
        }

        if (!payment.getReceiverUpiId().contains("@")) {

            throw new PaymentException(
                    "Invalid UPI ID.Please enter a valid upi ID");
        }

        if (payment.getAmount() == null ||
                payment.getAmount()
                        .compareTo(BigDecimal.ZERO) <= 0) {

            throw new PaymentException(
                    "Payment amount must be greater than zero");
        }
    }

    // Validate Bank Transfer
    private void validateBankTransfer(Payment payment) {

        if (payment.getUserId() == null) {
            throw new PaymentException("User ID is required");
        }

        if (payment.getAccountNumber() == null ||
                payment.getAccountNumber().isBlank()) {

            throw new PaymentException(
                    "Receiver account number is required");
        }

        if (payment.getAccountNumber().length() < 9) {

            throw new PaymentException(
                    "Invalid account number");
        }

        if (payment.getIfscCode() == null ||
                payment.getIfscCode().isBlank()) {

            throw new PaymentException(
                    "IFSC code is required");
        }

        if (payment.getAmount() == null ||
                payment.getAmount()
                        .compareTo(BigDecimal.ZERO) <= 0) {

            throw new PaymentException(
                    "Transfer amount must be greater than zero");
        }
    }

    // Check Account Balance
    private void checkBalance(Account account,
                              BigDecimal amount) {

        if (account.getBalance() == null) {

            throw new PaymentException(
                    "Account balance is not available");
        }

        if (account.getBalance()
                .compareTo(amount) < 0) {

            logger.warn(
                    "Insufficient balance for userId: {}",
                    account.getUserId());

            throw new PaymentException(
                    "Insufficient funds.Please enter a lower amount");
        }
    }

    // Get Payment History
    public List<Payment> getPaymentsByUserId(Long userId) {

        logger.info(
                "Fetching payment history for userId: {}",
                userId);

        return paymentRepository.findByUserId(userId);
    }

    // Get Payment By ID
    public Payment getPaymentById(Long paymentId) {

        logger.info(
                "Fetching payment with ID: {}",
                paymentId);

        return paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new PaymentException(
                                "Payment not found"));
    }
}