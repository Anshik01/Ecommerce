package com.order.orderservice.feign;

import com.order.orderservice.dto.ProductQuantityRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "PRODUCT-SERVICE")
public interface ProductClient {

    @PostMapping("/products/reduce-quantity")
    public ResponseEntity<String> reduceProductQuantity(@RequestBody List<ProductQuantityRequest> requestList);

    @PostMapping("/products/increase-quantity")
    public ResponseEntity<String> increaseProductQuantity(@RequestBody List<ProductQuantityRequest> requestList);
}
