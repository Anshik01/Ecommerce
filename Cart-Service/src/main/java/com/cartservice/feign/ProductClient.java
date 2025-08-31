package com.cartservice.feign;

import com.cartservice.entity.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "PRODUCT-SERVICE" )
public interface ProductClient {

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable int id);
}
