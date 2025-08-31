package com.eshoppingZone.ewallet.service;

import com.eshoppingZone.ewallet.entity.Ewallet;
import com.eshoppingZone.ewallet.entity.Statement;

import java.util.List;

public interface EwalletService {

    List<Ewallet> getWallets();

    Ewallet addWallet(Ewallet ewallet);

    void addMoney(int userId, double amount, String description);

    void update(int userId, double amount, String description, int id);

    Ewallet getById(int id);

    Ewallet getByUserId(int id);

    List<Statement> getStatementsById(int id);

    List<Statement> getStatements();

    boolean addRefundAmount(int userId,double amount,String deposit);

//    String deleteById(int id);
}