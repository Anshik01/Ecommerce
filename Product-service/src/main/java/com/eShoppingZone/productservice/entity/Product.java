package com.eShoppingZone.productservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int productId;
    @NotBlank(message = "Product type cannot be blank")
    private String productType;

    @NotBlank(message = "Product name cannot be blank")
    private String productName;

    @NotBlank(message = "Category cannot be blank")
    private String category;

    @Min(value = 1, message = "Product quantity must be at least 1")
    private int productQuantity;


    @ElementCollection
    private Map<Integer, @Min(1) @Max(5) Double> rating;

    @ElementCollection
    Map<Integer,String> review;

    @ElementCollection
    List<String> image;

    @Positive(message = "Price must be positive")
    private double price;

    String description;

    @ElementCollection
    Map<String,String> specification;



}
