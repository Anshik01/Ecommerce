package com.cartservice.repository;

import com.cartservice.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart,Integer> {
    Cart findByCartId(int cartId);
    Cart findByUserId(int userId);

}
