package com.order.orderservice.service;

import com.order.orderservice.dto.Cart;
import com.order.orderservice.entity.Address;
import com.order.orderservice.entity.Orders;


import java.util.List;
import java.util.Optional;


public interface OrderService {



    List<Orders> getAllOrders();

    String placeOrder(String token, String modeOfPayment);

    String changeStatus(String status, int orderId);

    void deleteOrder(int orderId);

    List<Orders> getOrderByCustomerId(int customerId);

    void storeAddress(Address address);

    Address getAddresByCustomerId(int customerId);

    List<Address> getAllAddress();


    Optional<Orders> getOrderById(int orderId);

    void onlinePayment(String token);

    String updateAddress(Address address);

   String updateOrderAddress(int orderId, Address address);

   boolean cancelOrder(int orderId,int userId);
   ;
}