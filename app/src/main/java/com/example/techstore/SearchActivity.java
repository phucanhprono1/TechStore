package com.example.techstore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.techstore.adapter.ProductAdapter;
import com.example.techstore.config.StaticConfig;
import com.example.techstore.models.Product;
import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements ProductAdapter.ProductClickListener {
    private EditText etSearch;
    private ImageButton btnSearch;
    private TextView tvResult;
    private RecyclerView rvProducts;
    String searchUrl = new LocalNetwork().getUrl()+"/products/search";
    ProductAdapter productAdapter;
    List<Product> searchResult = new ArrayList<>();
    RequestQueue q;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        etSearch = findViewById(R.id.et_search);
        btnSearch = findViewById(R.id.btn_search);
        tvResult = findViewById(R.id.tv_result);
        rvProducts = findViewById(R.id.rv_products);



        q = Volley.newRequestQueue(getApplicationContext());
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                   searchResult.clear();
                    StringRequest jar = new StringRequest(Request.Method.POST, searchUrl + "/" + etSearch.getText().toString(), new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONArray ja = new JSONArray(response);
                                for (int i = 0; i < ja.length(); i++) {
                                    JSONObject jo = ja.getJSONObject(i);
                                    Product p = new Product();
                                    p.setProductId(jo.getInt("productId"));
                                    p.setProductName(jo.getString("productName"));
                                    p.setPrice((float) jo.getDouble("price"));
                                    p.setImage(jo.getString("image"));
                                    p.setDescription(jo.getString("description"));
                                    searchResult.add(p);
                                }
                                productAdapter.notifyDataSetChanged();
                                tvResult.setText("Kết quả tìm kiếm: " + searchResult.size() + " sản phẩm");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                    q.add(jar);

            }
        });

        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        productAdapter = new ProductAdapter(searchResult,this);
        rvProducts.setAdapter(productAdapter);
    }

    @Override
    public void onItemClick(Product product) {
        Intent i1 = new Intent(getApplicationContext(),ProductDetailActivity.class);
        i1.putExtra("prod",product);
//        i1.putExtra("key1",key1);
//        i1.putExtra("id",uid);
        startActivity(i1);
    }
//    String logoutapi = new LocalNetwork().getUrl()+"/auth/logout";
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        try {
//            JSONObject jsonbody = new JSONObject();
//            jsonbody.put("role","customer");
//            jsonbody.put("key", StaticConfig.CURRENT_KEY);
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