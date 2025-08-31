package com.order.orderservice.feign;

import com.order.orderservice.dto.Cart;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "CART-SERVICE")
public interface CartClient {


    @GetMapping("/cart/user")
    public ResponseEntity<Cart> getCartByUserId(@RequestHeader(value = "Authorization" ,required = false) String token);

    @PutMapping("/cart/Clear-Cart")
    public ResponseEntity<Boolean> clearCart(@RequestHeader(value = "Authorization" ,required = false) String token);

}

