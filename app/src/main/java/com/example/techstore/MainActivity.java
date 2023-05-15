package com.example.techstore;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import com.example.techstore.retrofit.ApiService;
import com.facebook.login.LoginManager;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements CategoryAdapter.CategoryClickListener, SwipeRefreshLayout.OnRefreshListener {
    String logoutapi = new LocalNetwork().getUrl()+"/auth/logout/";
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
    SwipeRefreshLayout swipeRefreshLayout;
    int uid=0;
    List<Category> category=new ArrayList<>();
    private ApiService logoutApi;


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
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout2);
        swipeRefreshLayout.setOnRefreshListener(this);
//        ImageView logout = findViewById(R.id.log_out);
        TextView name = header.findViewById(R.id.name11);
        q = Volley.newRequestQueue(getApplicationContext());
        Bundle b = getIntent().getExtras();

        FrameLayout frameLayout = findViewById(R.id.fragment_container);
        frameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // Vô hiệu hóa tính năng SwipeRefreshLayout khi người dùng vuốt
                swipeRefreshLayout.setEnabled(false);
                return false;
            }
        });
        TextView ct = findViewById(R.id.category_title_textview);
        ct.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                swipeRefreshLayout.setEnabled(true);
                return false;
            }
        });
        swipeRefreshLayout.setEnabled(true);
        TextView phone  = header.findViewById(R.id.phonenumberLabel);

        key = b.getString("key");
        StaticConfig.CURRENT_KEY = key;

        uid=b.getInt("id");
//        StaticConfig.UID= String.valueOf(uid);

        JsonObjectRequest rqs = new JsonObjectRequest(Request.Method.GET, currentuser+"?key="+StaticConfig.CURRENT_KEY,null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    name.setText(response.getString("name"));
                    userReference.child(StaticConfig.CURRENT_KEY).setValue(response.getInt("id"));
                    phone.setText(response.getString("phone_number"));
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
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ApiService apiService = new Retrofit.Builder()
                        .baseUrl(logoutapi)
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
            }
        });
        SearchFragment searchFragment=(SearchFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new SearchFragment()).commit();
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
                                    params.put("key", StaticConfig.CURRENT_KEY);

                                    return params;
                                }
                            };
                            q.add(sr);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        startActivity(intent2);
                        break;
                    case R.id.search_bar:

                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new SearchFragment())
                                .commit();

                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.order_history:


                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new OrderViewFragment())
                            .commit();

                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                }

                return true;
            }

        });

//        List<Category> category=new ArrayList<>();
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
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false);
        rcv.setLayoutManager(linearLayoutManager);
        rcv.setAdapter(categoryAdapter);
//        Intent serviceIntent = new Intent(this, MyService.class);
//        startService(serviceIntent);
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
                .baseUrl(logoutapi)
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

    @Override
    public void onRefresh() {
        category.clear();
        swipeRefreshLayout.setRefreshing(false);
//        RequestQueue q = Volley.newRequestQueue(this);
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
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // make API call
        Intent intent = new Intent(this, MyService.class);
        startService(intent);
    }

}