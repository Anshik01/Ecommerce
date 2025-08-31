package com.cartservice.service;

import com.cartservice.dto.AddToCart;
import com.cartservice.entity.Cart;
import com.cartservice.entity.Items;
import com.cartservice.entity.Product;
import com.cartservice.exception.NoSuchCartFound;
import com.cartservice.feign.ProductClient;
import com.cartservice.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {


    private final ProductClient productClient;
    private final CartRepository repo;

    @Autowired
    public CartServiceImpl(CartRepository repo,ProductClient  productClient) {

        this.repo = repo;
        this.productClient=productClient;
    }

    @Override
    public Cart getCartById(int id) {
        return repo.findById(id)
                .orElseThrow(() -> new NoSuchCartFound("Cart not exist with id: " + id));
    }

    @Override
    public Cart getCartByUserId(int userId) {
        Cart cart = repo.findByUserId(userId);
        if (cart == null) {
            throw new NoSuchCartFound("Cart not found");
        }
        return cart;
    }

    @Override
    public Cart updateCart(Cart cart) {
        if (cart.getCartId() <= 0) {
            throw new NoSuchCartFound("Invalid Cart for update.");
        }

        Optional<Cart> existingCart = repo.findById(cart.getCartId());
        if (existingCart.isPresent()) {
            Cart cartToUpdate = existingCart.get();
            cart.setCartId(cartToUpdate.getCartId());
            return repo.save(cart);
        } else {
            throw new NoSuchCartFound("Cannot update. Cart not found for user");
        }
    }


    @Override
    public List<Cart> getAllCarts() {
        return repo.findAll();
    }

    @Override
    public double cartTotal(Cart cart) {
        return cart.calculateTotalPrice();
    }

    @Override
    public Cart addCart(Cart cart) {
        int userId = cart.getUserId();
        Cart cart1 = repo.findByUserId(userId);
        if (cart1 == null) {
            return repo.save(cart);
        } else {
            throw new NoSuchCartFound("Cart already Exits");

        }

    }

    @Override
    public void addToCart(AddToCart request, int userId) {
        Product product = productClient.getProductById(request.getProductId()).getBody();

        Cart cart = repo.findByUserId(userId);
        if (cart == null) {
            cart = new Cart(userId);
        }

        if (product != null) {
            cart.addItem(new Items(
                    product.getProductId(),
                    product.getProductName(),
                    product.getPrice(),
                    request.getQuantity()
            ));
        }

        cart.calculateTotalPrice();
        repo.save(cart);
    }


    @Override
    public void decreaseItemQuantity(int userId, int productId) {
        Cart cart = repo.findByUserId(userId);
        if (cart == null) {
            throw new NoSuchCartFound("Cart not found, Please add items");
        }

        // Check if the product exists in the cart
        Optional<Items> itemOpt = cart.getItems().stream()
                .filter(item -> item.getProductId() == productId)
                .findFirst();

        if (itemOpt.isPresent()) {
            cart.decreaseQuantity(productId);
            cart.calculateTotalPrice();
            repo.save(cart);
        } else {
            throw new NoSuchCartFound("Product not found in cart");
        }

    }

    @Override
    public void removeItemFromCart(int userId, int productId) {
        Cart cart = repo.findByUserId(userId);

        if (cart == null) {
            throw new NoSuchCartFound("Cart not found for user ID: " + userId);
        }

        List<Items> items = cart.getItems();
        boolean removed = items.removeIf(item -> item.getProductId() == productId);

        if (!removed) {
            throw new NoSuchCartFound("Product not found in cart: " + productId);
        }
        cart.calculateTotalPrice();
        cart.setItems(items);
        repo.save(cart);
    }


    @Override
    public void increaseItemQuantity(int userId, int productId) {
        Cart cart = repo.findByUserId(userId);
        if (cart == null) {
            throw new NoSuchCartFound("Cart not found");
        }

        // Check if the product exists in the cart
        Optional<Items> itemOpt = cart.getItems().stream()
                .filter(item -> item.getProductId() == productId)
                .findFirst();

        if (itemOpt.isPresent()) {
            cart.increaseQuantity(productId);
            cart.calculateTotalPrice();
            repo.save(cart);
        } else {
            throw new NoSuchCartFound("Product not found in cart");
        }
    }



    @Override
    public boolean clearCart(int userId) {
        Cart cart = repo.findByUserId(userId);
        if (cart == null) {
            throw new NoSuchCartFound("Cart not found!");
        }

        if (cart.getItems().isEmpty()) {
            throw new NoSuchCartFound("No items found!");
        }

        cart.getItems().clear();
        cart.setTotalPrice(0);
        repo.save(cart);
        return true;
    }


   public List<Items> getAllItems(int userId){
       Cart cart = repo.findByUserId(userId);
       if (cart == null) {
           throw new NoSuchCartFound("Cart not found!");
       }

       if (cart.getItems().isEmpty()) {
           throw new NoSuchCartFound("No items found!");
       }
       return cart.getItems();
    }

}
