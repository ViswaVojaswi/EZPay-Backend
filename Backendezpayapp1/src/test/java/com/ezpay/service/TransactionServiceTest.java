
package com.ezpay.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ezpay.entity.Transaction;

@SpringBootTest
class TransactionServiceTest {

    @Autowired
    private TransactionService service;

    // Test 1: Create Transaction
    @Test
    void testCreateTransaction() {

        Transaction transaction = new Transaction();

        transaction.setUserId(1L);
        transaction.setAmount(new BigDecimal("500.00"));
        transaction.setSender("TEST_SENDER");
        transaction.setRecipient("TEST_RECIPIENT");
        transaction.setStatus("COMPLETED");

        Transaction result =
                service.createTransaction(transaction);

        assertNotNull(result);
        assertNotNull(result.getTransactionId());

        assertEquals(
                transaction.getUserId(),
                result.getUserId()
        );

        assertEquals(
                transaction.getAmount(),
                result.getAmount()
        );

        assertEquals(
                transaction.getSender(),
                result.getSender()
        );

        assertEquals(
                transaction.getRecipient(),
                result.getRecipient()
        );

        assertEquals(
                transaction.getStatus(),
                result.getStatus()
        );
    }

    // Test 2: Get Transaction History
    @Test
    void testGetTransactionHistory() {

        Long userId = 1L;

        List<Transaction> result =
                service.getTransactionHistory(userId);

        assertNotNull(result);
    }

    // Test 3: Get Transaction Status
    @Test
    void testGetTransactionStatus() {

        Long transactionId = 1L;

        String result =
                service.getTransactionStatus(transactionId);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    // Test 4: Get Transaction Details
    @Test
    void testGetTransaction() {

        Long transactionId = 1L;

        Transaction result =
                service.getTransaction(transactionId);

        assertNotNull(result);

        assertEquals(
                transactionId,
                result.getTransactionId()
        );
    }
}
