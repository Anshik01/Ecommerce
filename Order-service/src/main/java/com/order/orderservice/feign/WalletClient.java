package com.order.orderservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "WALLET-SERVICE")
public interface WalletClient {

    @GetMapping("/wallet/Check-Balance")
    public Double getBalance(@RequestHeader(value = "Authorization" ,required = false) String token) ;

    @PutMapping("/wallet/paymoney/{id}/{amount}/{orderId}")
    public ResponseEntity<String> payMoney(@PathVariable int id, @PathVariable double amount, @PathVariable int orderId);

    @PutMapping("/wallet/refund-Amount/{userId}/{amount}")
    public ResponseEntity<Boolean> addRefundAmount(@PathVariable int userId, @PathVariable double amount);

}
