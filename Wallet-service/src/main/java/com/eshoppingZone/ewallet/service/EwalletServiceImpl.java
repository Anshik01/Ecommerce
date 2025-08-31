package com.eshoppingZone.ewallet.service;

import com.eshoppingZone.ewallet.entity.Ewallet;
import com.eshoppingZone.ewallet.entity.Statement;
import com.eshoppingZone.ewallet.exception.InsufficientBalanceExcep;
import com.eshoppingZone.ewallet.exception.WalletException;
import com.eshoppingZone.ewallet.repository.EwalletRepository;
import com.eshoppingZone.ewallet.repository.StatementsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EwalletServiceImpl implements EwalletService {

    @Autowired
    private EwalletRepository ewalletRepo;

    @Autowired
    private StatementsRepository statementRepo;

    @Override
    public List<Ewallet> getWallets() {
        List<Ewallet> wallets = ewalletRepo.findAll();
        if (wallets.isEmpty()) {
            throw new WalletException("No wallets found");
        }
        return wallets;
    }


    @Override
    public Ewallet addWallet(Ewallet ewallet) {
        Optional<Ewallet> existingWallet = ewalletRepo.findByUserId(ewallet.getUserId());
        if (existingWallet.isPresent()) {
            throw new WalletException("Wallet already exists for userId: " + ewallet.getUserId());
        }
        return ewalletRepo.save(ewallet);
    }


    @Override
    public void addMoney(int userId, double amount, String description) {
        Optional<Ewallet> existingWallet = ewalletRepo.findByUserId(userId);
        Ewallet ewallet;
        if (!existingWallet.isPresent()){
            ewallet=new Ewallet();
            ewallet.setUserId(userId);
        }else {
            ewallet=existingWallet.get();
        }

        if (amount <= 0) {
            throw  new WalletException("Please add valid amount");
        }

        ewallet.setCurrentBalance(ewallet.getCurrentBalance() + amount);
        ewalletRepo.save(ewallet);

        Statement statement = new Statement();
        statement.setTransactionType("CREDIT");
        statement.setAmount(amount);
        statement.setDateTime(LocalDateTime.now());
        statement.setTransactionRemarks(description);
        statement.setEwallet(ewallet);
        statementRepo.save(statement);
    }

    @Override
    public void update(int userId, double amount, String description, int orderId) {
        Ewallet wallet = ewalletRepo.findByUserId(userId)
                .orElseThrow(() -> new WalletException("Wallet not found. Please add amount"));

        if (wallet.getCurrentBalance() < amount  ) {
            throw new InsufficientBalanceExcep("Insufficient Balance");
        }

        if (amount <= 0) {
            throw  new WalletException("Invalid amount");
        }
        wallet.setCurrentBalance(wallet.getCurrentBalance() - amount);
        ewalletRepo.save(wallet);

        Statement statement = new Statement();
        statement.setTransactionType("DEBIT");
        statement.setAmount(amount);
        statement.setDateTime(LocalDateTime.now());
        statement.setTransactionRemarks(description);
        statement.setOrderId(orderId);
        statement.setEwallet(wallet);
        statementRepo.save(statement);
    }

    @Override
    public Ewallet getById(int id) {
      return  ewalletRepo.findById(id)
                .orElseThrow(()->new WalletException("Wallet not found. please add amount"));
    }

    @Override
    public Ewallet getByUserId(int id) {
        return ewalletRepo.findByUserId(id)
                .orElseThrow(() -> new WalletException("Wallet not found. please add amount "));
    }

    @Override
    public List<Statement> getStatementsById(int userId) {
        Ewallet wallet = getByUserId(userId);
        return wallet.getStatements();
    }

    @Override
    public List<Statement> getStatements() {
        return statementRepo.findAll();
    }

    @Override
   public boolean addRefundAmount(int userId,double amount,String description){
        Optional<Ewallet> existingWallet = ewalletRepo.findByUserId(userId);
        Ewallet ewallet;
        if (!existingWallet.isPresent()){
            ewallet=new Ewallet();
            ewallet.setUserId(userId);
        }else {
            ewallet=existingWallet.get();
        }

        if (amount <= 0) {
            throw  new WalletException("Please add valid amount");
        }

        ewallet.setCurrentBalance(ewallet.getCurrentBalance() + amount);
        ewalletRepo.save(ewallet);

        Statement statement = new Statement();
        statement.setTransactionType("REFUND");
        statement.setAmount(amount);
        statement.setDateTime(LocalDateTime.now());
        statement.setTransactionRemarks(description);
        statement.setEwallet(ewallet);
        statementRepo.save(statement);
        return true;
    }
//    @Override
//    public String deleteById(int userId) {
//        Ewallet wallet = ewalletRepo.findByUserId(userId)
//                .orElseThrow(() -> new WalletException("Wallet not found "));
//
//        ewalletRepo.delete(wallet);
//        return "Wallet deleted successfully ";
//    }

}



//        Ewallet wallet = ewalletRepo.findByUserId(userId)
//                .orElseThrow(() -> new WalletException("Wallet not found "));