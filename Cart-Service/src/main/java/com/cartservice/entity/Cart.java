package com.cartservice.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@Entity
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cartId;

    private int userId;

    @PositiveOrZero(message = "Total price cannot be negative")
    private double totalPrice;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Items> items = new ArrayList<>();


    public Cart() {}
    public Cart(int userId) {
        this.userId=userId;
    }

    public Cart(int cartId, List<Items> items) {
        this.cartId = cartId;
        this.items = items;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }


    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public List<Items> getItems() {
        return items;
    }

    public void setItems(List<Items> items) {
        this.items = items;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double calculateTotalPrice() {
        double total = 0.0;
        for (Items item : items) {
            total += item.getPrice() * item.getQuantity();
        }
        this.totalPrice = total;
        return this.totalPrice;
    }

    public void addItem(Items newItem) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        for (Items item : items) {
            if (item.getProductId() == newItem.getProductId()) {
                item.setQuantity(item.getQuantity() + newItem.getQuantity());
                return;
            }
        }
        newItem.setCart(this);
        this.items.add(newItem);
    }

    public void decreaseQuantity(int productId) {
        Iterator<Items> iterator = items.iterator();
        while (iterator.hasNext()) {
            Items item = iterator.next();
            if (item.getProductId() == productId) {
                if (item.getQuantity() > 1) {
                    item.setQuantity(item.getQuantity() - 1);
                } else {
                    item.setCart(null);
                    iterator.remove();
                }
                break;
            }
        }
    }

    public void removeItem(int productId) {
        Iterator<Items> iterator = items.iterator();
        while (iterator.hasNext()) {
            Items item = iterator.next();
            if (item.getProductId() == productId) {
                item.setCart(null);
                iterator.remove();
                break;
            }
        }
    }

    public void increaseQuantity(int productId) {
        for (Items item : items) {
            if (item.getProductId() == productId) {
                item.setQuantity(item.getQuantity() + 1);
                break;
            }
        }
    }


    @Override
    public String toString() {
        return "Cart{" +
                "cartId=" + cartId +
                ", totalPrice=" + totalPrice +
                ", items=" + items +
                '}';
    }

    @Override
    public int hashCode() {
        return cartId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Cart)) {
            return false;
        }
        Cart cart = (Cart) obj;
        return cartId == cart.cartId;
    }
}