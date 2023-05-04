package com.example.techstore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.techstore.adapter.ProductAdapter;
import com.example.techstore.config.StaticConfig;
import com.example.techstore.models.Category;
import com.example.techstore.models.Product;
import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductActivity extends AppCompatActivity implements ProductAdapter.ProductClickListener, SwipeRefreshLayout.OnRefreshListener {
    String productByCategoryId = new LocalNetwork().getUrl()+"/products/view/";
    String key1;
    Bundle bundle = new Bundle();
    ProductAdapter productAdapter;
    int uid;
    SwipeRefreshLayout swipeRefreshLayout;
    List<Product> productList = new ArrayList<>();
    Category c;
    RequestQueue q;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        c = (Category) getIntent().getParcelableExtra("cat");
//        currentCustomerDTO = (CurrentCustomerDTO) getIntent().getParcelableExtra("id");

        key1=getIntent().getStringExtra("key");
        uid = getIntent().getIntExtra("id",0);

        Toast.makeText(getApplicationContext(),"key1: "+key1,Toast.LENGTH_SHORT).show();
//        userId = b.getString("id");
        TextView category_name = (TextView) findViewById(R.id.category_name);
        category_name.setText(c.getCategoryName());
        RecyclerView recyclerView = findViewById(R.id.products);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout1);
        swipeRefreshLayout.setOnRefreshListener(this);
        q = Volley.newRequestQueue(getApplicationContext());


//        List<Product> productList = new ArrayList<>();

        JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, productByCategoryId+c.getCategoryId(),null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject catJson = response.getJSONObject(i);
                                Product p = new Product();
                                p.setProductId(catJson.getInt("productId"));
                                p.setProductName(catJson.getString("productName"));
                                p.setPrice((float) catJson.getDouble("price"));
                                p.setImage(catJson.getString("image"));
                                p.setDescription(catJson.getString("description"));
                                productList.add(p);
                                productAdapter.setProduct(productList);
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
        });
        q.add(stringRequest);
        productAdapter = new ProductAdapter(productList,this);
        recyclerView.setAdapter(productAdapter);

    }

    @Override
    public void onItemClick(Product product) {
        Intent i1 = new Intent(getApplicationContext(),ProductDetailActivity.class);
        i1.putExtra("prod",product);
        i1.putExtra("key1",key1);
        i1.putExtra("id",uid);
        startActivity(i1);
    }

    @Override
    public void onRefresh() {
        productList.clear();
        swipeRefreshLayout.setRefreshing(false);
        RequestQueue q = Volley.newRequestQueue(this);
//        List<Product> productList = new ArrayList<>();

        JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, productByCategoryId+c.getCategoryId(),null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject catJson = response.getJSONObject(i);
                                Product p = new Product();
                                p.setProductId(catJson.getInt("productId"));
                                p.setProductName(catJson.getString("productName"));
                                p.setPrice((float) catJson.getDouble("price"));
                                p.setImage(catJson.getString("image"));
                                p.setDescription(catJson.getString("description"));
                                productList.add(p);

                            }

                            catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            productAdapter.setProduct(productList);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        q.add(stringRequest);
    }
//    String logoutapi = new LocalNetwork().getUrl()+"/auth/logout";
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        try {
//            JSONObject jsonbody = new JSONObject();
//            jsonbody.put("role","customer");
//            jsonbody.put("key",StaticConfig.CURRENT_KEY);
//            String requestBody = jsonbody.toString();
//            StringRequest sr = new StringRequest(Request.Method.POST, logoutapi, new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//
////                    userReference.child(key).removeValue();
//                    LoginManager.getInstance().logOut();
//
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//
//                }
//            }){
//                @Override
//                public HashMap<String, String> getParams() {
//                    // Thêm các tham số cho POST request
//                    HashMap<String, String> params = new HashMap<>();
//                    params.put("role", "customer");
//                    params.put("key", StaticConfig.CURRENT_KEY);
//
//                    return params;
//                }
//            };
//            q.add(sr);
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }
//        q.cancelAll(this);
//    }
}