package com.example.techstore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.techstore.adapter.ProductAdapter;
import com.example.techstore.models.Category;
import com.example.techstore.models.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends AppCompatActivity implements ProductAdapter.ProductClickListener {
    String productByCategoryId = new LocalNetwork().getUrl()+"/products/view/";
    String key1;
    Bundle bundle = new Bundle();
    ProductAdapter productAdapter;
    int uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        Category c = (Category) getIntent().getParcelableExtra("cat");
//        currentCustomerDTO = (CurrentCustomerDTO) getIntent().getParcelableExtra("id");

        key1=getIntent().getStringExtra("key");
        uid = getIntent().getIntExtra("id",0);

        Toast.makeText(getApplicationContext(),"key1: "+key1,Toast.LENGTH_SHORT).show();
//        userId = b.getString("id");
        TextView category_name = (TextView) findViewById(R.id.category_name);
        category_name.setText(c.getCategoryName());
        RecyclerView recyclerView = findViewById(R.id.products);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        RequestQueue q = Volley.newRequestQueue(getApplicationContext());


        List<Product> productList = new ArrayList<>();

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
}