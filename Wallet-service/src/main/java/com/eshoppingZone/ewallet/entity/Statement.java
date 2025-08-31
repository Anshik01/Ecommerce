package com.eshoppingZone.ewallet.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Statement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int statementId;
    private String transactionType;
    private double amount;
    private LocalDateTime dateTime;
    private int orderId;
    private String transactionRemarks;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    @JsonBackReference
    private Ewallet ewallet;

    public Statement() {}

    public Statement(int statementId, String transactionType, double amount,
                     LocalDateTime dateTime, int orderId, String transactionRemarks) {
        this.statementId = statementId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.dateTime = dateTime;
        this.orderId = orderId;
        this.transactionRemarks = transactionRemarks;
    }

    public Statement(int statementId, Ewallet ewallet, String transactionType, double amount,
                     LocalDateTime dateTime, int orderId, String transactionRemarks) {
        this.statementId = statementId;
        this.ewallet = ewallet;
        this.transactionType = transactionType;
        this.amount = amount;
        this.dateTime = dateTime;
        this.orderId = orderId;
        this.transactionRemarks = transactionRemarks;
    }

    public int getStatementId() {
        return statementId;
    }

    public void setStatementId(int statementId) {
        this.statementId = statementId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getTransactionRemarks() {
        return transactionRemarks;
    }

    public void setTransactionRemarks(String transactionRemarks) {
        this.transactionRemarks = transactionRemarks;
    }

    public Ewallet getEwallet() {
        return ewallet;
    }

    public void setEwallet(Ewallet ewallet) {
        this.ewallet = ewallet;
    }

    @Override
    public String toString() {
        return "Statement{" +
                "statementId=" + statementId +
                ", transactionType='" + transactionType + '\'' +
                ", amount=" + amount +
                ", dateTime=" + dateTime +
                ", orderId=" + orderId +
                ", transactionRemarks='" + transactionRemarks + '\'' +
                '}';
    }
}