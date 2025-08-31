package com.cartservice.entity;

import jakarta.persistence.ElementCollection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    int productId;
    String productType;
    String productName;
    String category;
    int productQuantity;
    double price;



}
