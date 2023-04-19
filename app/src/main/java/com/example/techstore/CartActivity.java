package com.example.techstore;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class CartActivity extends AppCompatActivity {
    String cartapi = new LocalNetwork().getUrl()+"/cart/add";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
    }
}