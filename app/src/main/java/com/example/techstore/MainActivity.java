package com.example.techstore;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
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
import com.example.techstore.config.StaticConfig;
import com.example.techstore.models.Category;
import com.example.techstore.models.CurrentCustomerDTO;
import com.example.techstore.models.Customer;
import com.example.techstore.retrofit.ApiService;
import com.facebook.login.LoginManager;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements CategoryAdapter.CategoryClickListener {
    String logoutapi = new LocalNetwork().getUrl()+"/auth/logout";
    String currentuser = new LocalNetwork().getUrl()+"/auth/currentUser";
    String getAllCategory = new LocalNetwork().getUrl()+"/category/getAll";
    private CategoryAdapter categoryAdapter;
    String key;
    View header;
    DrawerLayout drawerLayout;
    CurrentCustomerDTO currentCustomerDTO;
    Toolbar toolbar;
    DatabaseReference userReference = FirebaseDatabase.getInstance("https://techecommerceserver-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("currentUser");
    private RequestQueue q;
    int uid=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NavigationView nv = findViewById(R.id.nav_view);
        header = nv.getHeaderView(0);
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar= findViewById(R.id.toolbar);
        currentCustomerDTO=new CurrentCustomerDTO();
        setSupportActionBar(toolbar);
        nv.bringToFront();
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout ,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


//        ImageView logout = findViewById(R.id.log_out);
        TextView name = header.findViewById(R.id.name11);
        q = Volley.newRequestQueue(getApplicationContext());
        Bundle b = getIntent().getExtras();

        TextView phone  = header.findViewById(R.id.phonenumberLabel);

        key = b.getString("key");

        uid=b.getInt("id");
        StaticConfig.UID= String.valueOf(uid);

        JsonObjectRequest rqs = new JsonObjectRequest(Request.Method.GET, currentuser+"?key="+key,null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    name.setText(response.getString("name"));
                    userReference.child(key).setValue(response.getInt("id"));
//                    phone.setText(response.getString("phone"));
//                    passdata(response.getInt("id"));

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
//        phone.setText(key);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.cart_button:
                        Intent intent1=new Intent(getApplicationContext(),CartActivity.class);
                        intent1.putExtra("key",key);
                        intent1.putExtra("id",uid);
                        startActivity(intent1);
                        break;
                    case R.id.log_out:

                        Intent intent2=new Intent(MainActivity.this,LoginOptionActivity.class);
                        try {
                            JSONObject jsonbody = new JSONObject();
                            jsonbody.put("role","customer");
                            jsonbody.put("key",key);
                            String requestBody = jsonbody.toString();
                            StringRequest sr = new StringRequest(Request.Method.POST, logoutapi, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Intent i = new Intent(MainActivity.this, LoginOptionActivity.class);
                                    userReference.child(key).removeValue();
                                    LoginManager.getInstance().logOut();
                                    startActivity(i);
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
                                    params.put("role", "customer");
                                    params.put("key", key);

                                    return params;
                                }
                            };
                            q.add(sr);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        startActivity(intent2);
                        break;

                }

                return true;
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
        Intent i=new Intent(MainActivity.this,ProductActivity.class);
        i.putExtra("cat", cate);
        i.putExtra("key1",key);
        i.putExtra("id",uid);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        ApiService apiService = new Retrofit.Builder()
                .baseUrl("logoutapi")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);
        Call<String> call = apiService.logout(key,"customer");
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // handle failure
            }
        });

        super.onBackPressed();
    }
}