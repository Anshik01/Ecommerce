package com.cartservice.service;


import com.cartservice.dto.AddToCart;
import com.cartservice.entity.Cart;
import com.cartservice.entity.Items;

import java.util.List;

public interface CartService {

   Cart getCartById(int id);
   Cart getCartByUserId(int userId);
   Cart updateCart(Cart cart);
   List<Cart> getAllCarts();
   double cartTotal(Cart cart);
   Cart addCart(Cart cart);

   void addToCart(AddToCart req,int userId);
   void decreaseItemQuantity(int userId, int productId);
   void removeItemFromCart(int userId, int productId);
   List<Items> getAllItems(int userId);

   void increaseItemQuantity(int userId, int productId);

   boolean clearCart(int userId);
}
