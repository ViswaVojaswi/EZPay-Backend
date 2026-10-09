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

    private Transaction createTestTransaction() {
        Transaction transaction = new Transaction();

        transaction.setUserId(1L);
        transaction.setAmount(new BigDecimal("500.00"));
        transaction.setSender("TEST_SENDER");
        transaction.setRecipient("TEST_RECIPIENT");
        transaction.setStatus("COMPLETED");

        return service.createTransaction(transaction);
    }

    // Test 1: Create Transaction
    @Test
    void testCreateTransaction() {

        Transaction result = createTestTransaction();

        assertNotNull(result);
        assertNotNull(result.getTransactionId());
        assertEquals(1L, result.getUserId());
        assertEquals(new BigDecimal("500.00"), result.getAmount());
        assertEquals("TEST_SENDER", result.getSender());
        assertEquals("TEST_RECIPIENT", result.getRecipient());
        assertEquals("COMPLETED", result.getStatus());
    }

    // Test 2: Get Transaction History
    @Test
    void testGetTransactionHistory() {

        createTestTransaction();

        List<Transaction> result =
                service.getTransactionHistory(1L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    // Test 3: Get Transaction Status
    @Test
    void testGetTransactionStatus() {

        Transaction saved = createTestTransaction();

        String result =
                service.getTransactionStatus(saved.getTransactionId());

        assertEquals("COMPLETED", result);
    }

    // Test 4: Get Transaction Details
    @Test
    void testGetTransaction() {

        Transaction saved = createTestTransaction();

        Transaction result =
                service.getTransaction(saved.getTransactionId());

        assertNotNull(result);
        assertEquals(
                saved.getTransactionId(),
                result.getTransactionId()
        );
    }
}