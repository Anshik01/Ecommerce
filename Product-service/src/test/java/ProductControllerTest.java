import com.eShoppingZone.productservice.controller.ProductController;
import com.eShoppingZone.productservice.entity.Product;
import com.eShoppingZone.productservice.service.ProductService;
import com.eShoppingZone.productservice.service.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private ProductController productController;

    private Product mockProduct;

    @BeforeEach
    void setUp() {
        mockProduct = new Product();
        mockProduct.setProductId(1);
        mockProduct.setProductName("Laptop");
        mockProduct.setCategory("Electronics");
        mockProduct.setProductType("Gadget");
        mockProduct.setPrice(1000.00);
    }

    @Test
    void testAddProduct_Success() {
        when(securityService.isMerchantOrAdmin(anyString())).thenReturn(true);
        doNothing().when(productService).addProduct(any(Product.class));

        ResponseEntity<String> response = productController.addProduct("mockToken", mockProduct);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product added successfully", response.getBody());

        verify(productService, times(1)).addProduct(mockProduct);
    }

    @Test
    void testAddProduct_Unauthorized() {
        when(securityService.isMerchantOrAdmin(anyString())).thenReturn(false);

        ResponseEntity<String> response = productController.addProduct("mockToken", mockProduct);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Unauthorized access. Please provide a valid token.", response.getBody());
    }

    @Test
    void testGetAllProducts() {
        List<Product> products = Arrays.asList(mockProduct);
        when(productService.getAllProduct()).thenReturn(products);

        ResponseEntity<List<Product>> response = productController.getAllProducts();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetProductById_Success() {
        when(productService.getProductById(anyInt())).thenReturn(Optional.of(mockProduct));

        ResponseEntity<?> response = productController.getProductById(1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockProduct, response.getBody());
    }


    @Test
    void testUpdateProduct_Success() {
        when(securityService.isMerchantOrAdmin(anyString())).thenReturn(true);
        when(productService.updateProducts(any(Product.class))).thenReturn(mockProduct);

        ResponseEntity<?> response = productController.updateProduct("mockToken", mockProduct);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockProduct, response.getBody());
    }

    @Test
    void testDeleteProduct_Success() {
        when(securityService.isMerchantOrAdmin(anyString())).thenReturn(true);
        doNothing().when(productService).deleteProductById(anyInt());

        ResponseEntity<String> response = productController.deleteProduct("mockToken", 1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product deleted successfully", response.getBody());
    }

    @Test
    void testDeleteProduct_Unauthorized() {
        when(securityService.isMerchantOrAdmin(anyString())).thenReturn(false);

        ResponseEntity<String> response = productController.deleteProduct("mockToken", 1);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Unauthorized access. Please provide a valid token.", response.getBody());
    }

    @Test
    void testGetProductByCategory_Success() {
        List<Product> products = Arrays.asList(mockProduct);
        when(productService.getProductByCategory(anyString())).thenReturn(products);

        ResponseEntity<List<Product>> response = productController.getProductByCategory("Electronics");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetProductByType_Success() {
        List<Product> products = Arrays.asList(mockProduct);
        when(productService.getProductByType(anyString())).thenReturn(products);

        ResponseEntity<List<Product>> response = productController.getProductByType("Gadget");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetProductsByIds_Success() {
        List<Product> products = Arrays.asList(mockProduct);
        when(productService.getProductsByIds(anyList())).thenReturn(products);

        ResponseEntity<List<Product>> response = productController.getProductsByIds(Arrays.asList(1));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testReduceProductQuantity_Success() {
        doNothing().when(productService).reduceProductQuantities(anyList());

        ResponseEntity<String> response = productController.reduceProductQuantity(Arrays.asList());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product quantities updated successfully", response.getBody());
    }

    @Test
    void testCheckStockAvailability_Success() {
        when(productService.isStockAvailable(anyInt(), anyInt())).thenReturn(true);

        ResponseEntity<Boolean> response = productController.checkStockAvailability(1, 5);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
    }

    @Test
    void testIncreaseProductQuantity_Success() {
        doNothing().when(productService).increaseProductQuantities(anyList());

        ResponseEntity<String> response = productController.increaseProductQuantity(Arrays.asList());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product quantities updated successfully", response.getBody());
    }
}