package com.example.techecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.FacebookSdk;

public class LoginOptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_option);
        FacebookSdk.sdkInitialize(getApplicationContext());
        
        Button signinacc = findViewById(R.id.sign_in_account);
        signinacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginOptionActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }
}