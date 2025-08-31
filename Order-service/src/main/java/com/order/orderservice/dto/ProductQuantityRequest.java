package com.order.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ProductQuantityRequest {
    private int productId;
    private int quantity;

}
