package com.ezpay.service;

import com.ezpay.entity.Transaction;
import com.ezpay.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    // Create transaction
    public Transaction createTransaction(Transaction transaction) {
        return repository.save(transaction);
    }

    // Use Case 3.1
    // View transaction history
    public List<Transaction> getTransactionHistory(Long userId) {
        return repository.findByUserId(userId);
    }

    // Use Case 3.2
    // Track transaction status
    public String getTransactionStatus(Long transactionId) {

        Transaction transaction = repository.findById(transactionId)
                .orElseThrow(() ->
                        new RuntimeException("Transaction not found"));

        return transaction.getStatus();
    }

    // Get transaction details
    public Transaction getTransaction(Long transactionId) {

        return repository.findById(transactionId)
                .orElseThrow(() ->
                        new RuntimeException("Transaction not found"));
    }
}