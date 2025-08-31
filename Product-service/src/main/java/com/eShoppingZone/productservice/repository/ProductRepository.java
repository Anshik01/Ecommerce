package com.eShoppingZone.productservice.repository;

import com.eShoppingZone.productservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Integer> {

   // Optional<Product> findByProductName(String name);
    List<Product> findByCategory(String category);
    List<Product> findByProductType(String type);

    List<Product> findByProductNameContainingIgnoreCase(String name);
}
