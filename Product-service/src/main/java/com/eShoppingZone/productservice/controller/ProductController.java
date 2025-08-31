package com.eShoppingZone.productservice.controller;

import com.eShoppingZone.productservice.dto.ProductQuantityRequest;
import com.eShoppingZone.productservice.entity.Product;
import com.eShoppingZone.productservice.service.ProductService;
import com.eShoppingZone.productservice.service.SecurityService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;
    private final SecurityService securityService;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    public ProductController(ProductService productService, SecurityService securityService) {
        this.productService = productService;
        this.securityService = securityService;
    }


    @PostMapping("/addProduct")
    public ResponseEntity<String> addProduct(@RequestHeader("Authorization") String token,@Valid @RequestBody Product product) {
        logger.info("Received request to add product: {}", product);
        boolean isValid=securityService.isMerchantOrAdmin(token);
        if(isValid) {
            productService.addProduct(product);
            logger.info("Product added successfully: {}", product);
            return ResponseEntity.ok("Product added successfully");
        }
        logger.warn("Unauthorized access attempt to add product.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Unauthorized access. Please provide a valid token.");
    }

    @GetMapping("/getAllProduct")
    public ResponseEntity<List<Product>> getAllProducts() {
        logger.info("Fetching all products.");
        List<Product> products = productService.getAllProduct();
        logger.info("Returning {} products.", products.size());
        return ResponseEntity.ok(products);

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable int id) {
        logger.info("Fetching product by ID: {}", id);
        return ResponseEntity.ok(productService.getProductById(id).get());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<Product>> getProductByName(@PathVariable String name) {
        logger.info("Fetching product by name: {}", name);
        return ResponseEntity.ok(productService.getProductByName(name));
    }

    @PutMapping
    public ResponseEntity<?> updateProduct(@RequestHeader(value = "Authorization" ,required = false) String token,@Valid @RequestBody Product product) {
        logger.info("Received request to update product: {}", product);
        boolean isValid=securityService.isMerchantOrAdmin(token);
        if(isValid) {
            logger.info("Product updated successfully: {}", product);
            return ResponseEntity.ok(productService.updateProducts(product));
        }
        logger.warn("Unauthorized access attempt to update product.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Unauthorized access. Please provide a valid token.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@RequestHeader(value = "Authorization" ,required = false) String token,@PathVariable int id) {
        logger.info("Received request to delete product with ID: {}", id);
        boolean isValid=securityService.isMerchantOrAdmin(token);
        if(isValid) {
            productService.deleteProductById(id);
            logger.info("Product deleted successfully: ID {}", id);
            return ResponseEntity.ok("Product deleted successfully");
        }
        logger.warn("Unauthorized access attempt to delete product ID: {}", id);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Unauthorized access. Please provide a valid token.");

    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductByCategory(@PathVariable String category) {
        logger.info("Fetching products by category: {}", category);
        return ResponseEntity.ok(productService.getProductByCategory(category));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Product>> getProductByType(@PathVariable String type) {
        logger.info("Fetching products by type: {}", type);
        return ResponseEntity.ok(productService.getProductByType(type));
    }


    @GetMapping("/getProductsByIds")
    public ResponseEntity<List<Product>> getProductsByIds(@RequestParam List<Integer> ids) {
        logger.info("Fetching products by IDs: {}", ids);
        return ResponseEntity.ok(productService.getProductsByIds(ids));
    }

    @PostMapping("/reduce-quantity")
    public ResponseEntity<String> reduceProductQuantity(@RequestBody List<ProductQuantityRequest> requestList) {
        logger.info("Reducing product quantities: {}", requestList);
        productService.reduceProductQuantities(requestList);
        logger.info("Product quantities updated successfully.");
        return ResponseEntity.ok("Product quantities updated successfully");
    }

    @GetMapping("/{id}/check-stock/{quantity}")
    public ResponseEntity<Boolean> checkStockAvailability(@PathVariable int id, @PathVariable int quantity) {
        logger.info("Checking stock availability for product ID: {} with requested quantity: {}", id, quantity);
        return ResponseEntity.ok(productService.isStockAvailable(id, quantity));
    }

    @PostMapping("/increase-quantity")
    public ResponseEntity<String> increaseProductQuantity(@RequestBody List<ProductQuantityRequest> requestList) {
        logger.info("Increasing product quantities: {}", requestList);
        productService.increaseProductQuantities(requestList);
        return ResponseEntity.ok("Product quantities updated successfully");
    }

}
