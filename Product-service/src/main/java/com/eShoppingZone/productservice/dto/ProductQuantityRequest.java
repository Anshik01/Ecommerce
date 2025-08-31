package com.eShoppingZone.productservice.dto;

import lombok.Data;

@Data
public class ProductQuantityRequest {
    private int productId;
    private int quantity;

}
