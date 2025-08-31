package com.order.orderservice.controller;

import com.order.orderservice.dto.Cart;
import com.order.orderservice.entity.Address;
import com.order.orderservice.entity.Orders;
import com.order.orderservice.exception.UnauthorizedException;
import com.order.orderservice.feign.CartClient;
import com.order.orderservice.feign.ProfileServiceClient;
import com.order.orderservice.service.OrderService;
import com.order.orderservice.service.SecurityService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/order")
@CrossOrigin(origins = "*")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
private OrderService service;

@Autowired
SecurityService securityService;

@Autowired
CartClient cartClient;



    @PostMapping("/{mode}")
    public ResponseEntity<String> placeOrder(@RequestHeader(value = "Authorization" ,required = false) String token, @PathVariable String mode) {
//       int userId=securityService.getUserIdFromToken(token);
        String response = service.placeOrder(token, mode);
        cartClient.clearCart(token);
        logger.info("Order placed successfully ");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{orderId}/status/{status}")
    public ResponseEntity<String> changeStatus(@RequestHeader(value = "Authorization" ,required = false) String token,@PathVariable int orderId, @PathVariable String status) {
        logger.info("Changing order status - Order ID: {}, New Status: {}", orderId, status);
        boolean isValid=securityService.isMerchantOrAdmin(token);
       if(isValid){
           String response = service.changeStatus(status, orderId);
           logger.info("Order status changed successfully.");
           return ResponseEntity.ok(response);
       }
        logger.warn("Unauthorized access attempt for Order ID: {}", orderId);
        throw  new UnauthorizedException("Unauthorized User!");
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> deleteOrder(@RequestHeader(value = "Authorization" ,required = false) String token,@PathVariable int orderId) {
        logger.info("Deleting order - Order ID: {}", orderId);
        boolean isValid=securityService.validateToken(token);
        if(isValid){
            service.deleteOrder(orderId);
            logger.info("Order deleted successfully.");
            return ResponseEntity.ok("Order deleted successfully");
        }
        logger.warn("Unauthorized delete request for Order ID: {}", orderId);
        throw  new UnauthorizedException("Unauthorized User!");
    }

    @GetMapping("/user")
    public ResponseEntity<List<Orders>> getOrderByCustomerId(@RequestHeader(value = "Authorization" ,required = false) String token) {
        logger.info("Fetching orders for a customer.");
        int userId=securityService.getUserIdFromToken(token);
        return ResponseEntity.ok(service.getOrderByCustomerId(userId));
    }

    @PostMapping("/fill/address")
    public ResponseEntity<String> storeAddress(@RequestHeader(value = "Authorization" ,required = false) String token,@RequestBody @Valid Address address) {
        logger.info("Received request to store address");
        boolean isValid=securityService.validateToken(token);
        if(isValid){
            int userId=securityService.getUserIdFromToken(token);
            address.setUserId(userId);
            service.storeAddress(address);
            logger.info("Address stored successfully for User ID: {}", userId);
            return ResponseEntity.ok("Address stored successfully");
        }
        logger.warn("Unauthorized request to store address");
        throw  new UnauthorizedException("Unauthorized User!");

    }

    @GetMapping("/address")
    public ResponseEntity<Address> getAddressByUserId(@RequestHeader(value = "Authorization" ,required = false) String token) {
        logger.info("Fetching address for user");
        int userId=securityService.getUserIdFromToken(token);
        logger.info("Address fetched successfully for User ID: {}", userId);
        return ResponseEntity.ok(service.getAddresByCustomerId(userId));
    }

    @GetMapping("/All-address")
    public ResponseEntity<List<Address>> getAllAddresses(@RequestHeader(value = "Authorization" ,required = false) String token) {
        logger.info("Fetching all addresses");
        boolean isValid=securityService.isMerchantOrAdmin(token);
        if(isValid){
            logger.info("All addresses fetched successfully");
            return ResponseEntity.ok(service.getAllAddress());
        }
        logger.warn("Unauthorized attempt to access all addresses");
        throw  new UnauthorizedException("Unauthorized User!");
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Optional<Orders>> getOrderById(@RequestHeader(value = "Authorization" ,required = false) String token,@PathVariable int orderId) {
        logger.info("Fetching order details - Order ID: {}", orderId);
        boolean isValid=securityService.validateToken(token);
        if(isValid){
            return ResponseEntity.ok(service.getOrderById(orderId));
        }
        logger.warn("Unauthorized order detail request for Order ID: {}", orderId);
        throw  new UnauthorizedException("Unauthorized User!");

    }

    @PostMapping("/online-payment")
    public ResponseEntity<String> onlinePayment(@RequestHeader(value = "Authorization" ,required = false) String token) {
        logger.info("Processing online payment");
        service.onlinePayment(token);
        cartClient.clearCart(token);
        logger.info("Order placed with online payment successfully");
        return ResponseEntity.ok("Order placed with online payment");
    }

    @PutMapping("/address/update")
    public ResponseEntity<String> updateAddress(@RequestHeader(value = "Authorization" ,required = false) String token,@RequestBody @Valid Address address) {
        logger.info("Received request to update address");
        boolean isValid=securityService.validateToken(token);
        if(isValid){
            logger.info("Address updated successfully");
            return ResponseEntity.ok(service.updateAddress(address));
        }
        logger.warn("Unauthorized attempt to update address");
        throw  new UnauthorizedException("Unauthorized User!");

    }

    @PutMapping("/update-Order-Address/{orderId}")
    public ResponseEntity<String> updateOrderAddress(@RequestHeader(value = "Authorization" ,required = false) String token,@PathVariable int orderId, @RequestBody @Valid Address address) {
        logger.info("Received request to update order address - Order ID: {}", orderId);
        boolean isValid=securityService.validateToken(token);
        if(isValid){
            logger.info("Order address updated successfully for Order ID: {}", orderId);
            return ResponseEntity.ok(service.updateOrderAddress(orderId, address));
        }
        logger.warn("Unauthorized attempt to update order address for Order ID: {}", orderId);
        throw  new UnauthorizedException("Unauthorized User!");

    }

    @GetMapping("/allorder")
    public ResponseEntity<List<Orders>> getAllOrder(@RequestHeader(value = "Authorization" ,required = false) String token){
        logger.info("Fetching all orders");
        boolean isValid=securityService.isMerchantOrAdmin(token);
        if(isValid){
            logger.info("All orders fetched successfully");
            return ResponseEntity.ok(service.getAllOrders());
        }
        logger.warn("Unauthorized attempt to access all orders");
        throw  new UnauthorizedException("Unauthorized User!");


    }

    @PutMapping("/cancel-order/{orderId}")
    public ResponseEntity<Boolean> cancelOrder(@RequestHeader(value = "Authorization" ,required = false) String token,@PathVariable int orderId){
        logger.info("Processing order cancellation - Order ID: {}", orderId);
        boolean isValid=securityService.validateToken(token);
        if(isValid){
            int userId= securityService.getUserIdFromToken(token);
            service.cancelOrder(orderId,userId);
            logger.info("Order canceled successfully - Order ID: {}", orderId);
            return  ResponseEntity.ok(true);
        }
        logger.warn("Unauthorized attempt to cancel order - Order ID: {}", orderId);
        throw  new UnauthorizedException("Unauthorized User!");

    }



//    @GetMapping("/user")
//    public ResponseEntity<List<Orders>> getOrdersByUserId(@RequestHeader(value = "Authorization" ,required = false) String token) {
//       int userId =securityService.getUserIdFromToken(token);
//        List<Orders> orders = service.getAllOrdersByUserId(userId);
//
//        if (orders.isEmpty()) {
//            return ResponseEntity.noContent().build();
//        }
//
//        return ResponseEntity.ok(orders);
//    }



}
