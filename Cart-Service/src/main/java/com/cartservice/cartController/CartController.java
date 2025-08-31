package com.cartservice.cartController;

import com.cartservice.dto.AddToCart;
import com.cartservice.entity.Cart;
import com.cartservice.entity.Items;
import com.cartservice.feign.ProfileServiceClient;
import com.cartservice.service.CartService;
import com.cartservice.service.SecurityService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
@CrossOrigin(origins = "*")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    ProfileServiceClient profileServiceClient;

    @Autowired
    SecurityService securityService;

    private final CartService service;

    @Autowired
    public CartController(CartService service){
        this.service=service;
    }

    @GetMapping("/getAllCart")
    public ResponseEntity<List<Cart>> getAllCarts(){
        logger.info("Fetching all carts");
        return ResponseEntity.ok(service.getAllCarts());
    }

    @PostMapping
    public ResponseEntity<String> addCart(@RequestHeader(value = "Authorization" ,required = false) String token,@Valid @RequestBody  Cart cart){
        logger.info("Adding cart for user");
        int userId = securityService.getUserIdFromToken(token);
        cart.setUserId(userId);
        service.addCart(cart);
        return ResponseEntity.ok("Cart added successfully");
    }

//    @GetMapping("/id/{cartId}")
//    public ResponseEntity<?> getCartById(@RequestHeader(value = "Authorization" ,required = false) String token,@PathVariable int cartId) {
//        logger.info("Fetching cart by ID: {}", cartId);
//        boolean isLoggedIn=securityService.validateToken(token);
//       if(isLoggedIn) {
//           return ResponseEntity.ok(service.getCartById(cartId));
//       }
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid or expired token. Please login again."));
//    }

    @GetMapping("/user")
    public ResponseEntity<Cart> getCartByUserId(@RequestHeader(value = "Authorization" ,required = false) String token) {
        int userId=securityService.getUserIdFromToken(token);
        logger.info("Fetching cart for user ID: {}", userId);
        return ResponseEntity.ok(service.getCartByUserId(userId));
    }

    @GetMapping("/items")
    public ResponseEntity<List<Items>> getItems(@RequestHeader(value = "Authorization" ,required = false) String token){
        List<Items> items=service.getAllItems(securityService.getUserIdFromToken(token));
        logger.info("fetching all items from cart");
        return ResponseEntity.ok(items);
    }


    @PutMapping
    public ResponseEntity<Cart> updateCart(@RequestHeader(value = "Authorization" ,required = false) String token,@Valid @RequestBody Cart cart){
        logger.info("Updating cart for user");
        int userId = securityService.getUserIdFromToken(token);
        cart.setUserId(userId);
        return ResponseEntity.ok(service.updateCart(cart));
    }

    @GetMapping("/total")
    public ResponseEntity<Double> cartTotal(@RequestHeader(value = "Authorization" ,required = false) String token){
        logger.info("Calculating cart total for user");
        int userId = securityService.getUserIdFromToken(token);
        Cart cart=service.getCartByUserId(userId);
        return ResponseEntity.ok(service.cartTotal(cart));
    }

    @PostMapping("/add")
    public ResponseEntity<String> addToCart(@RequestBody AddToCart request,@RequestHeader(value = "Authorization" ,required = false) String token) {
        logger.info("Adding product to cart for user");
        int userId = securityService.getUserIdFromToken(token);
        service.addToCart(request,userId);
        return ResponseEntity.ok("Product added to cart");
    }

    @PutMapping("/decrease/{productId}")
    public ResponseEntity<String> decreaseItemQuantity(@RequestHeader(value = "Authorization" ,required = false) String token, @PathVariable int productId) {
        logger.info("Decreasing item quantity for product ID: {}", productId);
        int userId = securityService.getUserIdFromToken(token);
        service.decreaseItemQuantity(userId, productId);
        return ResponseEntity.ok("Product quantity decreased");
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<String> removeItemFromCart(@RequestHeader(value = "Authorization" ,required = false) String token, @PathVariable int productId) {
        logger.info("Removing item from cart for product ID: {}", productId);
        int userId = securityService.getUserIdFromToken(token);
        service.removeItemFromCart(userId, productId);
        return ResponseEntity.ok("Item removed from cart successfully.");
    }

    @PutMapping("/increase/{productId}")
    public ResponseEntity<String> increaseItemQuantity(@RequestHeader(value = "Authorization" ,required = false) String token, @PathVariable int productId) {
        logger.info("Increasing item quantity for product ID: {}", productId);
        int userId = securityService.getUserIdFromToken(token);
        service.increaseItemQuantity(userId, productId);
        return ResponseEntity.ok("Item quantity increased in cart.");
    }

    @PutMapping("/Clear-Cart")
    public ResponseEntity<Boolean> clearCart(@RequestHeader(value = "Authorization" ,required = false) String token){
        logger.info("Clearing cart for user");
        int userId=securityService.getUserIdFromToken(token);
        if(service.clearCart(userId)){
            return ResponseEntity.ok(true);
        }
       return ResponseEntity.ok(false);
    }


}
