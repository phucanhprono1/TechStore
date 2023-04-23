package com.example.techstore.retrofit;

import com.example.techstore.models.Cart;
import com.example.techstore.models.CartItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CartAPI {
    @GET("/customer/cart/view/{customerId}")
    Call<List<CartItem>> getCartByCustomerId(@Path("customerId") String customerId);
}