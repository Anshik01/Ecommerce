package com.eShoppingZone.productservice.service;

import com.eShoppingZone.productservice.dto.ProductQuantityRequest;
import com.eShoppingZone.productservice.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    void addProduct(Product product);
   List<Product> getAllProduct();
   Optional<Product> getProductById(int id);
    List<Product> getProductByName(String name);
   Product updateProducts(Product product);
   void deleteProductById(int id);
   List<Product> getProductByCategory(String category);
   List<Product> getProductByType(String type);


   List<Product> getProductsByIds(List<Integer> ids);

   void reduceProductQuantities(List<ProductQuantityRequest> requestList);
 void increaseProductQuantities(List<ProductQuantityRequest> requestList);
   boolean isStockAvailable(int productId, int quantity);


}
