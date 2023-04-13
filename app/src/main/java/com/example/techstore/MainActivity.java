package com.example.techstore;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.techstore.models.Customer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    String logoutapi = new LocalNetwork().getUrl()+"/auth/logout";
    String currentuser = new LocalNetwork().getUrl()+"/auth/currentUser";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView logout = findViewById(R.id.logout);
        TextView name = findViewById(R.id.name);

        Bundle b = getIntent().getExtras();

        final String key = b.getString("key");
        RequestQueue q = Volley.newRequestQueue(getApplicationContext());
        StringRequest rqs = new StringRequest(Request.Method.GET, currentuser, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Customer cs = new Gson().fromJson(response, Customer.class);
                name.setText(cs.name);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public HashMap<String, String> getParams() {
                // Thêm các tham số cho POST request
                HashMap<String, String> params = new HashMap<>();
                params.put("key", key);
                return params;
            }
        };
        q.add(rqs);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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