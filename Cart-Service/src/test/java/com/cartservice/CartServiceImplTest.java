package com.cartservice;

import com.cartservice.dto.AddToCart;
import com.cartservice.entity.Cart;
import com.cartservice.entity.Items;
import com.cartservice.entity.Product;
import com.cartservice.exception.NoSuchCartFound;
import com.cartservice.feign.ProductClient;
import com.cartservice.repository.CartRepository;
import com.cartservice.service.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private CartServiceImpl cartService;

    private Cart mockCart;
    private Product mockProduct;
    private AddToCart mockAddToCart;

    @BeforeEach
    void setUp() {
        mockCart = new Cart(1);
        mockCart.setCartId(1);
        mockCart.addItem(new Items(101, "Phone", 500, 2));

        mockProduct = new Product();
        mockProduct.setProductId(101);
        mockProduct.setProductName("Phone");
        mockProduct.setPrice(500);

        mockAddToCart = new AddToCart();
        mockAddToCart.setProductId(101);
        mockAddToCart.setQuantity(2);
    }

    @Test
    void testGetCartById_Success() {
        when(cartRepository.findById(1)).thenReturn(Optional.of(mockCart));

        Cart response = cartService.getCartById(1);
        assertEquals(1, response.getCartId());
    }

    @Test
    void testGetCartById_NotFound() {
        when(cartRepository.findById(1)).thenReturn(Optional.empty());

        NoSuchCartFound exception = assertThrows(NoSuchCartFound.class, () -> cartService.getCartById(1));
        assertEquals("Cart not exist with id: 1", exception.getMessage());
    }

    @Test
    void testGetCartByUserId_Success() {
        when(cartRepository.findByUserId(1)).thenReturn(mockCart);

        Cart response = cartService.getCartByUserId(1);
        assertEquals(1, response.getUserId());
    }

    @Test
    void testGetCartByUserId_NotFound() {
        when(cartRepository.findByUserId(1)).thenReturn(null);

        NoSuchCartFound exception = assertThrows(NoSuchCartFound.class, () -> cartService.getCartByUserId(1));
        assertEquals("Cart not found", exception.getMessage());
    }

    @Test
    void testRemoveItemFromCart_Success() {
        when(cartRepository.findByUserId(1)).thenReturn(mockCart);

        cartService.removeItemFromCart(1, 101);

        verify(cartRepository, times(1)).save(mockCart);
        assertTrue(mockCart.getItems().isEmpty());
    }

    @Test
    void testRemoveItemFromCart_NotFound() {
        when(cartRepository.findByUserId(1)).thenReturn(mockCart);

        NoSuchCartFound exception = assertThrows(NoSuchCartFound.class, () -> cartService.removeItemFromCart(1, 999));
        assertEquals("Product not found in cart: 999", exception.getMessage());
    }

    @Test
    void testIncreaseItemQuantity_Success() {
        when(cartRepository.findByUserId(1)).thenReturn(mockCart);

        cartService.increaseItemQuantity(1, 101);

        verify(cartRepository, times(1)).save(mockCart);
        assertEquals(3, mockCart.getItems().get(0).getQuantity());
    }

    @Test
    void testDecreaseItemQuantity_Success() {
        when(cartRepository.findByUserId(1)).thenReturn(mockCart);

        cartService.decreaseItemQuantity(1, 101);

        verify(cartRepository, times(1)).save(mockCart);
        assertEquals(1, mockCart.getItems().get(0).getQuantity());
    }

    @Test
    void testClearCart_Success() {
        when(cartRepository.findByUserId(1)).thenReturn(mockCart);

        boolean result = cartService.clearCart(1);

        verify(cartRepository, times(1)).save(mockCart);
        assertTrue(result);
        assertTrue(mockCart.getItems().isEmpty());
    }

    @Test
    void testClearCart_NotFound() {
        when(cartRepository.findByUserId(1)).thenReturn(null);

        NoSuchCartFound exception = assertThrows(NoSuchCartFound.class, () -> cartService.clearCart(1));
        assertEquals("Cart not found!", exception.getMessage());
    }

    @Test
    void testCartTotal() {
        double total = cartService.cartTotal(mockCart);
        assertEquals(1000, total);
    }
}