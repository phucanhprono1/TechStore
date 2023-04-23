package com.example.techstore.models;

import java.util.List;

public class Cart {
    public Integer getCartId() {
        return cartId;
    }

    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }

    public int getProduct_quantity() {
        return product_quantity;
    }

    public void setProduct_quantity(int product_quantity) {
        this.product_quantity = product_quantity;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setProducts(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    private Integer cartId;
    private int product_quantity;


    private List<CartItem> cartItems;


    private Customer customer;
}
