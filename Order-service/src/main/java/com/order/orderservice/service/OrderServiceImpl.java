package com.order.orderservice.service;

import com.order.orderservice.dto.Cart;
import com.order.orderservice.dto.Items;
import com.order.orderservice.dto.ProductQuantityRequest;
import com.order.orderservice.entity.Address;
import com.order.orderservice.entity.OrderItem;
import com.order.orderservice.entity.Orders;
import com.order.orderservice.exception.OrderException;
import com.order.orderservice.feign.CartClient;
import com.order.orderservice.feign.ProductClient;
import com.order.orderservice.feign.WalletClient;
import com.order.orderservice.repository.AddressRepository;
import com.order.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    SecurityService securityService;
    @Autowired
    private WalletClient walletClient;

    @Autowired
    private CartClient cartClient;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    ProductClient productClient;

    @Override
    public List<Orders> getAllOrders() {
        List<Orders> orders = orderRepository.findAll();
        if (orders.isEmpty()) {
            throw new OrderException("No orders found.");
        }
        return orders;
    }

    @Override
    public String placeOrder(String token, String modeOfPayment) {
   int userId= securityService.getUserIdFromToken(token);
        Cart cart=cartClient.getCartByUserId(token).getBody();

        if(cart==null || cart.getItems() == null || cart.getItems().isEmpty()){
            throw new OrderException("cart is empty.");
        }

        Address address=addressRepository.findByUserId(userId);
        if(address==null){
            throw new OrderException("Please update your address ");
        }

        Orders order=new Orders();
        order.setOrderDate(LocalDate.now());
        order.setUserId(userId);
        order.setAmountPaid(cart.getTotalPrice());
        order.setModeOfPayment(modeOfPayment);
        order.setOrderStatus("PLACED");
        order.setAddress(address);

        List<OrderItem> orderItems = new ArrayList<>();
        for (Items itemDto : cart.getItems()) {
            OrderItem item = new OrderItem();
            item.setProductId(itemDto.getProductId());
            item.setProductName(itemDto.getProductName());
            item.setQuantity(itemDto.getQuantity());
            item.setPrice(itemDto.getPrice());
            item.setOrder(order);
            orderItems.add(item);
        }
        order.setItems(orderItems);
List<ProductQuantityRequest> productQuantityRequests=new ArrayList<>();
for(OrderItem orderItem:order.getItems()){
    ProductQuantityRequest quantityRequest=new ProductQuantityRequest(orderItem.getProductId(),orderItem.getQuantity());
    productQuantityRequests.add(quantityRequest);
}

       try{
           productClient.reduceProductQuantity(productQuantityRequests);
       } catch (OrderException e) {
           throw  new OrderException("Product not in stock, check stock!");
       }
       order.setEmailId(order.getAddress().getEmailId());
        orderRepository.save(order);
       emailService.sendOrderEmail(order);
        return "Order placed successfully for user ID " + userId;
    }

    @Override
    public String changeStatus(String status, int orderId) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException("Order with ID " + orderId + " not found."));
        order.setOrderStatus(status);
        orderRepository.save(order);
        return "Order status changed to " + status;
    }

    @Override
    public void deleteOrder(int orderId) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException("Order with ID " + orderId + " not found."));
        orderRepository.delete(order);
    }

    @Override
    public List<Orders> getOrderByCustomerId(int userId) {
        List<Orders> orders = orderRepository.findByUserId(userId);
        if (orders.isEmpty()) {
            throw new OrderException("No orders found for customer ID " + userId);
        }
        return orders;
    }

    @Override
    public void storeAddress(Address address) {
        if (address == null) {
            throw new OrderException("Address cannot be null.");
        }

        Address existingAddress = addressRepository.findByUserId(address.getUserId());

        if (existingAddress != null) {
            // Update the existing address fields
            existingAddress.setPincode(address.getPincode());
            existingAddress.setCity(address.getCity());
            existingAddress.setState(address.getState());
            existingAddress.setFlatNumber(address.getFlatNumber());
            existingAddress.setEmailId(address.getEmailId());
            existingAddress.setMobileNumber(address.getMobileNumber());
            existingAddress.setFullName(address.getFullName());

            addressRepository.save(existingAddress);
        } else {
            addressRepository.save(address);
        }
    }

    @Override
    public Address getAddresByCustomerId(int UserId) {
        Address address = addressRepository.findByUserId(UserId);
        if (address==null) {
            throw new OrderException("No address found for customer ID " + UserId);
        }
        return address;
    }

    @Override
    public List<Address> getAllAddress() {
        List<Address> addresses = addressRepository.findAll();
        if (addresses.isEmpty()) {
            throw new OrderException("No addresses available.");
        }
        return addresses;
    }

    @Override
    public Optional<Orders> getOrderById(int orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    public void onlinePayment(String token) {
//        int userId=securityService.getUserIdFromToken(token);
        ResponseEntity<Cart> exitingCart=cartClient.getCartByUserId(token);
        if (exitingCart.getStatusCode() == HttpStatus.NOT_FOUND || !exitingCart.hasBody()) {
            throw new OrderException("Please add items to your cart before placing an order!");
        }

        Cart cart=exitingCart.getBody();


        if(cart==null || cart.getItems() == null || cart.getItems().isEmpty()){
            throw new OrderException("cart is empty.");
        }

        Address address=addressRepository.findByUserId(cart.getUserId());
        if(address==null){
            throw new OrderException("Please update your address ");
        }

        Orders order=new Orders();
        order.setOrderDate(LocalDate.now());
        order.setUserId(cart.getUserId());
        order.setAmountPaid(cart.getTotalPrice());
        order.setModeOfPayment("WALLET");
        order.setOrderStatus("PENDING");
        order.setAddress(address);

        List<OrderItem> orderItems = new ArrayList<>();
        for (Items itemDto : cart.getItems()) {
            OrderItem item = new OrderItem();
            item.setProductId(itemDto.getProductId());
            item.setProductName(itemDto.getProductName());
            item.setQuantity(itemDto.getQuantity());
            item.setPrice(itemDto.getPrice());
            item.setOrder(order);
            orderItems.add(item);
        }
        order.setItems(orderItems);

        double balance=0.0;
        try {
            balance= walletClient.getBalance(token);
        }catch (Exception e){
            throw new OrderException("Please add balance!");
        }

        if(balance< cart.getTotalPrice() || balance<=0){
            throw new OrderException("Insufficient balance,please add amount");
        }

        Orders savedOrder = orderRepository.save(order);
        try {
            walletClient.payMoney(cart.getUserId(), cart.getTotalPrice(), savedOrder.getOrderId());
        }catch (Exception e){
            throw new OrderException("Payment failed "+e.getMessage());
        }

        savedOrder.setOrderStatus("PLACED");

        List<ProductQuantityRequest> productQuantityRequests=new ArrayList<>();
        for(OrderItem orderItem:order.getItems()){
            ProductQuantityRequest quantityRequest=new ProductQuantityRequest(orderItem.getProductId(),orderItem.getQuantity());
            productQuantityRequests.add(quantityRequest);
        }

        try{
            productClient.reduceProductQuantity(productQuantityRequests);
        } catch (OrderException e) {
            throw  new OrderException("Product not in stock, check stock!");
        }
        savedOrder.setEmailId(savedOrder.getAddress().getEmailId());
        orderRepository.save(savedOrder);
        emailService.sendOrderEmail(savedOrder);
    }

    @Override
    public String updateAddress(Address address) {
        if (address == null || address.getId() == null) {
            throw new OrderException("Invalid address ");
        }

        Address existingAddress = addressRepository.findById(address.getId())
                .orElseThrow(() -> new OrderException("Address not found with ID " + address.getId()));

        existingAddress.setUserId(address.getUserId());
        existingAddress.setFullName(address.getFullName());
        existingAddress.setMobileNumber(address.getMobileNumber());
        existingAddress.setFlatNumber(address.getFlatNumber());
        existingAddress.setCity(address.getCity());
        existingAddress.setPincode(address.getPincode());
        existingAddress.setState(address.getState());

        addressRepository.save(existingAddress);

        return "Address updated with ID " + address.getId();
    }

    @Override
    public String updateOrderAddress(int orderId, Address address) {
        if (address == null) {
            throw new OrderException("Address cannot be null.");
        }

        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException("Order not found with ID: " + orderId));

        order.setAddress(address);
        orderRepository.save(order);

        return "Address updated for Order ID: " + orderId;
    }

    @Override
   public boolean cancelOrder(int orderId,int userId){
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException("Order with ID " + orderId + " not found."));
  if(order.getUserId()!=userId){
      throw new OrderException("Order not found!");
  }

        if(order.getOrderStatus().equalsIgnoreCase("CANCELED")){
            throw new OrderException("Order already cancel!");
        }
        if(order.getOrderStatus().equalsIgnoreCase("Delevered")){
            throw new OrderException("Order already Delevered!");
        }
        if(order.getOrderStatus().equalsIgnoreCase("PENDING")){
            throw new OrderException("Order is pending!");
        }
        order.setOrderStatus("CANCELED");
        orderRepository.save(order);
            List<ProductQuantityRequest> productQuantityRequests=new ArrayList<>();
            for(OrderItem orderItem:order.getItems()){
                ProductQuantityRequest quantityRequest=new ProductQuantityRequest(orderItem.getProductId(),orderItem.getQuantity());
                productQuantityRequests.add(quantityRequest);
            }
            productClient.increaseProductQuantity(productQuantityRequests);
          emailService.sendCancelEmail(order);
        if(!order.getModeOfPayment().equalsIgnoreCase("COD")){
          walletClient.addRefundAmount(order.getUserId(), order.getAmountPaid());
        }
        return true;
    }


//    public List<Orders> getAllOrdersByUserId(int userId) {
//        List<Orders> orders = orderRepository.findByUserId(userId);
//
//        if (orders == null) {
//            throw new OrderException("No orders found for user! ");
//        }
//
//        if (orders.isEmpty()) {
//            System.out.println("No orders found for user! ");
//        }
//
//        return orders;
//    }


}
