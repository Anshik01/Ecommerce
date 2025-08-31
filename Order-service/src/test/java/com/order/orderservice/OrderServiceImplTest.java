package com.order.orderservice;
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
import com.order.orderservice.service.OrderServiceImpl;
import com.order.orderservice.service.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private CartClient cartClient;

    @Mock
    private ProductClient productClient;

    @Mock
    private WalletClient walletClient;
    @Mock
    private SecurityService securityService;


    @InjectMocks
    private OrderServiceImpl orderService;

    private Orders mockOrder;
    private Cart mockCart;
    private Address mockAddress;

    @BeforeEach
    void setUp() {
        mockOrder = new Orders();
        mockOrder.setOrderId(1);
        mockOrder.setUserId(1);
        mockOrder.setOrderDate(LocalDate.now());
        mockOrder.setOrderStatus("PLACED");
        mockOrder.setModeOfPayment("COD");
        mockOrder.setAmountPaid(1000);

        mockCart = new Cart();
        mockCart.setUserId(1);
        mockCart.setItems(Arrays.asList(new Items(101, "Phone", 500, 2)));
        mockCart.setTotalPrice(1000);

        mockAddress = new Address();
        mockAddress.setId(1);
        mockAddress.setUserId(1);
        mockAddress.setFullName("John Doe");
        mockAddress.setMobileNumber("1234567890");
        mockAddress.setCity("Mumbai");
    }

    @Test
    void testGetAllOrders_Success() {
        when(orderRepository.findAll()).thenReturn(Arrays.asList(mockOrder));

        List<Orders> orders = orderService.getAllOrders();
        assertEquals(1, orders.size());
    }

    @Test
    void testGetAllOrders_NoOrdersFound() {
        when(orderRepository.findAll()).thenReturn(List.of());

        OrderException exception = assertThrows(OrderException.class, () -> orderService.getAllOrders());
        assertEquals("No orders found.", exception.getMessage());
    }

    @Test
    void testPlaceOrder_EmptyCart() {
        when(cartClient.getCartByUserId("1")).thenReturn(ResponseEntity.ok(new Cart()));

        OrderException exception = assertThrows(OrderException.class, () -> orderService.placeOrder("1", "COD"));
        assertEquals("cart is empty.", exception.getMessage());
    }

    @Test
    void testChangeStatus_Success() {
        when(orderRepository.findById(1)).thenReturn(Optional.of(mockOrder));

        String response = orderService.changeStatus("SHIPPED", 1);
        assertEquals("Order status changed to SHIPPED", response);
    }

    @Test
    void testDeleteOrder_Success() {
        when(orderRepository.findById(1)).thenReturn(Optional.of(mockOrder));
        doNothing().when(orderRepository).delete(mockOrder);

        orderService.deleteOrder(1);

        verify(orderRepository, times(1)).delete(mockOrder);
    }

    @Test
    void testGetOrderByCustomerId_Success() {
        when(orderRepository.findByUserId(1)).thenReturn(Arrays.asList(mockOrder));

        List<Orders> orders = orderService.getOrderByCustomerId(1);
        assertEquals(1, orders.size());
    }

    @Test
    void testStoreAddress_Success() {
        when(addressRepository.save(mockAddress)).thenReturn(mockAddress);

        orderService.storeAddress(mockAddress);
        verify(addressRepository, times(1)).save(mockAddress);
    }

    @Test
    void testCancelOrder_AlreadyCanceled() {
        mockOrder.setOrderStatus("CANCELED");
        when(orderRepository.findById(1)).thenReturn(Optional.of(mockOrder));

        OrderException exception = assertThrows(OrderException.class, () -> orderService.cancelOrder(1,2));
        assertEquals("Order already cancel!", exception.getMessage());
    }
}