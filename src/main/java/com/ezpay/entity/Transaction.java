package com.ezpay.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.SequenceGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TRANSACTIONS")
public class Transaction {

    @Id
@GeneratedValue(
    strategy = GenerationType.SEQUENCE,
    generator = "transaction_seq_gen"
)
@SequenceGenerator(
    name = "transaction_seq_gen",
    sequenceName = "TRANSACTION_SEQ",
    allocationSize = 1
)
@Column(name = "TRANSACTION_ID")
private Long transactionId;

    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @Column(name = "AMOUNT", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "SENDER", nullable = false, length = 100)
    private String sender;

    @Column(name = "RECIPIENT", nullable = false, length = 100)
    private String recipient;

    @Column(name = "STATUS", nullable = false, length = 30)
    private String status;

    @Column(name = "TRANSACTION_DATE")
    private LocalDateTime transactionDate;

    public Transaction() {
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
}