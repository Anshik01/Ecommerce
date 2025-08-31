package com.eshoppingZone.ewallet.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

@Entity
public class Ewallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int walletId;

    @Column(unique = true, nullable = false)
    private int userId;

    @PositiveOrZero(message="Balance can not be negative")
    private double currentBalance;

    @OneToMany(mappedBy = "ewallet", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Statement> statements;

    public Ewallet() {}

    public Ewallet(int walletId, int userId, double currentBalance, List<Statement> statements) {
        this.walletId = walletId;
        this.userId = userId;
        this.currentBalance = currentBalance;
        this.statements = statements;
    }

    public int getWalletId() {
        return walletId;
    }

    public void setWalletId(int walletId) {
        this.walletId = walletId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public void setStatements(List<Statement> statements) {
        this.statements = statements;
    }

    @Override
    public String toString() {
        return "Ewallet{" +
                "walletId=" + walletId +
                ", userId=" + userId +
                ", currentBalance=" + currentBalance +
                ", statements=" + statements +
                '}';
    }
}
