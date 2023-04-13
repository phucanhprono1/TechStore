package com.example.techecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    String logoutapi = new LocalNetwork().getUrl()+"/auth/logout";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button logout = findViewById(R.id.buttonLoggout);
        Bundle b = getIntent().getExtras();
        final String key = b.getString("key");
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestQueue q = Volley.newRequestQueue(getApplicationContext());

                try {
                    JSONObject jsonbody = new JSONObject();
                    jsonbody.put("role","customer");
                    jsonbody.put("key",key);
                    String requestBody = jsonbody.toString();
                    StringRequest sr = new StringRequest(Request.Method.POST, logoutapi, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Intent i = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(i);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }){
//                        @Override
//                        public String getBodyContentType() {
//                            return "application/x-www-form-urlencoded; charset=utf-8";
//                        }

                        @Override
                        public HashMap<String, String> getParams() {
                            // Thêm các tham số cho POST request
                            HashMap<String, String> params = new HashMap<>();
                            params.put("role", "customer");
                            params.put("key", key);
                            return params;
                        }
                    };
                    q.add(sr);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}