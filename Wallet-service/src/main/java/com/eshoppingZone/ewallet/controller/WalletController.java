package com.eshoppingZone.ewallet.controller;

import com.eshoppingZone.ewallet.entity.Ewallet;
import com.eshoppingZone.ewallet.entity.Statement;
import com.eshoppingZone.ewallet.exception.UnauthorizedException;
import com.eshoppingZone.ewallet.service.EwalletService;
import com.eshoppingZone.ewallet.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wallet")
@CrossOrigin(origins = "*")
public class WalletController {

    public static final String Deposit = "Amount deposited successfully";
    public static final String Withdraw = "Amount debited successfully";
    private static final Logger logger = LoggerFactory.getLogger(WalletController.class);

    @Autowired
    private EwalletService ewalletService;

    @Autowired
    private SecurityService securityService;


    @GetMapping("/all")
    public ResponseEntity<List<Ewallet>> getAllWallet(@RequestHeader(value = "Authorization" ,required = false) String token) {
        logger.info("Fetching all wallets.");
        boolean  isValid= securityService.isMerchantOrAdmin(token);
        if(isValid) {
            return ResponseEntity.ok(ewalletService.getWallets());
        }
        logger.warn("Unauthorized access attempt to fetch all wallets.");
        throw  new UnauthorizedException("Unauthorized User!");

    }

    @GetMapping("/Check-Balance")
    public Double getBalance(@RequestHeader(value = "Authorization" ,required = false) String token ) {
        logger.info("Checking balance for user.");
        boolean isValid= securityService.validateToken(token);
       if(isValid) {
           int userId = securityService.getUserIdFromToken(token);
           return ewalletService.getByUserId(userId).getCurrentBalance();
       }
        logger.warn("Invalid token provided for balance check.");
        throw new UnauthorizedException("Invalid token, please login!");
    }


    @PutMapping("/addAmount/{amount}")
    public ResponseEntity<String> addMoney(@RequestHeader(value = "Authorization" ,required = false) String token, @PathVariable double amount) {
        logger.info("Request to add amount: {}", amount);
        boolean isValid= securityService.validateToken(token);
        if(isValid) {
            int userId=securityService.getUserIdFromToken(token);
            ewalletService.addMoney(userId, amount, Deposit);
            logger.info("Successfully added amount: {}", amount);
            return ResponseEntity.ok("Successfully added");
        }
        logger.warn("Invalid token provided for adding money.");
        throw new UnauthorizedException("Invalid token, please login!");

    }

    @PutMapping("/paymoney/{userId}/{amount}/{orderId}")
    public ResponseEntity<String> payMoney(@PathVariable int userId, @PathVariable double amount, @PathVariable int orderId) {
        logger.info("Processing payment for userId: {}, amount: {}, orderId: {}", userId, amount, orderId);
        ewalletService.update(userId, amount, Withdraw, orderId);
        logger.info("Payment successfully processed.");
        return ResponseEntity.ok("Successfully paid amount");
    }

    @GetMapping("/statements")
    public ResponseEntity<List<Statement>> getStatementsById(@RequestHeader(value = "Authorization" ,required = false) String token) {
        logger.info("Fetching statements for user.");
        boolean isValid= securityService.validateToken(token);
        if(isValid) {
            int userId=securityService.getUserIdFromToken(token);
            return ResponseEntity.ok(ewalletService.getStatementsById(userId));
        }
        logger.warn("Invalid token provided for fetching statements.");
        throw new UnauthorizedException("Invalid token, please login!");
    }

    @GetMapping("/All-statements")
    public List<Statement> getStatements(@RequestHeader(value = "Authorization" ,required = false) String token) {
        logger.info("Fetching all statements.");
        boolean  isValid= securityService.isMerchantOrAdmin(token);
       if(isValid) {
           List<Statement> statements = ewalletService.getStatements();
           logger.info("Returning {} statements.", statements.size());
           return statements;
       }
        logger.warn("Unauthorized access attempt for fetching all statements.");
        throw  new UnauthorizedException("Unauthorized User!");
    }

    @PutMapping("/refund-Amount/{userId}/{amount}")
    public ResponseEntity<Boolean> addRefundAmount(@PathVariable int userId, @PathVariable double amount){
        logger.info("Processing refund for userId: {}, amount: {}", userId, amount);
        ewalletService.addRefundAmount(userId, amount, Deposit);
        logger.info("Refund processed successfully.");
        return ResponseEntity.ok(true);
    }


//    @DeleteMapping("/delete/{userId}")
//    public ResponseEntity<String> deleteById(@PathVariable int userId) {
//        return ResponseEntity.ok(ewalletService.deleteById(userId));
//    }
}


//    @PostMapping("/add")
//    public ResponseEntity<String> addNewWallet(@RequestBody Ewallet wallet) {
//        ewalletService.addWallet(wallet);
//        return ResponseEntity.ok("Successfully created.");
//    }