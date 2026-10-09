package com.ezpay.controller;

import com.ezpay.entity.Transaction;
import com.ezpay.service.TransactionService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin
public class TransactionController {

    private static final Logger logger =
            LogManager.getLogger(TransactionController.class);

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    // Test API
    @GetMapping
    public String test() {

        logger.info("Transaction API test request received");

        return "Transaction API is working";
    }

    // Create Transaction
    @PostMapping
    public Transaction createTransaction(
            @RequestBody Transaction transaction) {

        logger.info(
                "Create transaction request received. UserId: {}, Amount: {}, Sender: {}, Recipient: {}",
                transaction.getUserId(),
                transaction.getAmount(),
                transaction.getSender(),
                transaction.getRecipient()
        );

        try {

            Transaction savedTransaction =
                    service.createTransaction(transaction);

            logger.info(
                    "Transaction created successfully. TransactionId: {}",
                    savedTransaction.getTransactionId()
            );

            return savedTransaction;

        } catch (Exception e) {

            logger.error(
                    "Error while creating transaction for UserId: {}",
                    transaction.getUserId(),
                    e
            );

            throw e;
        }
    }

    // Get Transaction History
    @GetMapping("/history/{userId}")
    public List<Transaction> getHistory(
            @PathVariable("userId") Long userId) {

        logger.info(
                "Transaction history request received for UserId: {}",
                userId
        );

        try {

            List<Transaction> transactions =
                    service.getTransactionHistory(userId);

            logger.info(
                    "Transaction history retrieved successfully. UserId: {}, Number of transactions: {}",
                    userId,
                    transactions.size()
            );

            return transactions;

        } catch (Exception e) {

            logger.error(
                    "Error while retrieving transaction history for UserId: {}",
                    userId,
                    e
            );

            throw e;
        }
    }

    // Get Transaction Status
    @GetMapping("/status/{transactionId}")
    public String getStatus(
            @PathVariable("transactionId") Long transactionId) {

        logger.info(
                "Transaction status request received for TransactionId: {}",
                transactionId
        );

        try {

            String status =
                    service.getTransactionStatus(transactionId);

            logger.info(
                    "Transaction status retrieved successfully. TransactionId: {}, Status: {}",
                    transactionId,
                    status
            );

            return status;

        } catch (Exception e) {

            logger.error(
                    "Error while retrieving transaction status for TransactionId: {}",
                    transactionId,
                    e
            );

            throw e;
        }
    }

    // Get Transaction Details
    @GetMapping("/{transactionId}")
    public Transaction getTransaction(
            @PathVariable("transactionId") Long transactionId) {

        logger.info(
                "Transaction details request received for TransactionId: {}",
                transactionId
        );

        try {

            Transaction transaction =
                    service.getTransaction(transactionId);

            logger.info(
                    "Transaction details retrieved successfully. TransactionId: {}",
                    transactionId
            );

            return transaction;

        } catch (Exception e) {

            logger.error(
                    "Error while retrieving transaction details for TransactionId: {}",
                    transactionId,
                    e
            );

            throw e;
        }
    }
}