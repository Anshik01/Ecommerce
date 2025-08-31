package com.eShoppingZone.productservice.service;

import com.eShoppingZone.productservice.dto.ProductQuantityRequest;
import com.eShoppingZone.productservice.entity.Product;
import com.eShoppingZone.productservice.exception.ResourceNotFoundException;
import com.eShoppingZone.productservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
  public  ProductRepository repo;

    @Override
    public void addProduct(Product product) {
        repo.save(product);
    }

    @Override
    public List<Product> getAllProduct() {
        return repo.findAll();
    }

    @Override
    public Optional<Product> getProductById(int id) {
        Optional<Product> product = repo.findById(id);
        if (product.isEmpty() && product.get().getProductQuantity()<=0) {
            throw new ResourceNotFoundException("Product not found Or not in stock");
        }
        return product;
    }

    @Override
    public List<Product> getProductByName(String name) {
        List<Product> products = repo.findByProductNameContainingIgnoreCase(name);
        if (products.isEmpty()) {
            throw new ResourceNotFoundException("No products found containing name: " + name);
        }
        return products;
    }

    @Override
    public Product updateProducts(Product product) {
        Optional<Product> existingProductOpt = repo.findById(product.getProductId());

        if (existingProductOpt.isPresent()) {
            Product existingProduct = existingProductOpt.get();

            existingProduct.setProductName(product.getProductName());
            existingProduct.setProductType(product.getProductType());
            existingProduct.setProductQuantity(product.getProductQuantity());
            existingProduct.setCategory(product.getCategory());
            existingProduct.setDescription(product.getDescription());
            existingProduct.setImage(product.getImage());
            existingProduct.setPrice(product.getPrice());
            existingProduct.setRating(product.getRating());
            existingProduct.setReview(product.getReview());
            existingProduct.setSpecification(product.getSpecification());

            return repo.save(existingProduct);
        } else {
            throw new ResourceNotFoundException("Product not found with ID: " + product.getProductId());
        }
    }


    @Override
    public void deleteProductById(int id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Cannot delete. Product not found with ID: " + id);
        }
    }

    @Override
    public List<Product> getProductByCategory(String category) {
        List<Product> products = repo.findByCategory(category);
        if (products.isEmpty()) {
            throw new ResourceNotFoundException("No products found in category: " + category);
        }
        return products;
    }

    @Override
    public List<Product> getProductByType(String type) {
        List<Product> products = repo.findByProductType(type);
        if (products.isEmpty()) {
            throw new ResourceNotFoundException("No products found with type: " + type);
        }
        return products;
    }

    @Override
    public List<Product> getProductsByIds(List<Integer> ids) {
        return repo.findAllById(ids);
    }

    @Override
    public void reduceProductQuantities(List<ProductQuantityRequest> requestList) {
        for (ProductQuantityRequest request : requestList) {
            Product product = repo.findById(request.getProductId()).orElseThrow(
                    () -> new ResourceNotFoundException("Product not found with ID: " + request.getProductId())
            );

            if (product.getProductQuantity() < request.getQuantity()) {
                throw new ResourceNotFoundException("Insufficient stock for product ID: " + request.getProductId());
            }

            product.setProductQuantity(product.getProductQuantity() - request.getQuantity());
            repo.save(product);
        }
    }

    @Override
    public boolean isStockAvailable(int productId, int quantity) {
        return repo.findById(productId)
                .map(product -> product.getProductQuantity() >= quantity)
                .orElse(false);
    }

    @Override
  public void increaseProductQuantities(List<ProductQuantityRequest> requestList){
        for (ProductQuantityRequest request : requestList) {
            Product product = repo.findById(request.getProductId()).orElseThrow(
                    () -> new ResourceNotFoundException("Product not found with ID: " + request.getProductId())
            );

            if ( request.getQuantity()<=0) {
                throw new ResourceNotFoundException("Invalid request!");
            }

            product.setProductQuantity(product.getProductQuantity() + request.getQuantity());
            repo.save(product);
        }
    }


}
