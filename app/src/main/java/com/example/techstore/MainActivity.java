package com.example.techstore;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.techstore.adapter.CategoryAdapter;
import com.example.techstore.models.Category;
import com.example.techstore.models.Customer;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CategoryAdapter.CategoryClickListener {
    String logoutapi = new LocalNetwork().getUrl()+"/auth/logout";
    String currentuser = new LocalNetwork().getUrl()+"/auth/currentUser";
    String getAllCategory = new LocalNetwork().getUrl()+"/category/getAll";
    private CategoryAdapter categoryAdapter;
    String key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView logout = findViewById(R.id.logout);
        TextView name = findViewById(R.id.name);

        Bundle b = getIntent().getExtras();

        String key = b.getString("key");
        RequestQueue q = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest rqs = new JsonObjectRequest(Request.Method.GET, currentuser+"?key="+key,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    name.setText(response.getString("name"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){

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
                            Intent i = new Intent(MainActivity.this, LoginOptionActivity.class);
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
        List<Category> category=new ArrayList<>();

        JsonArrayRequest requestCate = new JsonArrayRequest(Request.Method.GET, getAllCategory,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject catJson = response.getJSONObject(i);
                        Category cat= new Category(catJson.getInt("categoryId"),catJson.getString("categoryName"));
//                        cat.setCategoryId());
//                        cat.setCategoryName();
                        category.add(cat);
                        categoryAdapter.setCate(category);


                    }

                    catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){

        };
        q.add(requestCate);
        categoryAdapter = new CategoryAdapter(category,this);
        RecyclerView rcv = findViewById(R.id.categories);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        rcv.setLayoutManager(linearLayoutManager);
        rcv.setAdapter(categoryAdapter);
    }

    @Override
    public void onItemClick(Category cate) {
        Intent i = new Intent(MainActivity.this,ProductActivity.class);
        i.putExtra("cat", cate);
        i.putExtra("key",key);
        startActivity(i);
    }
}