package com.example.techstore;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.techstore.models.Product;

import org.json.JSONException;
import org.json.JSONObject;

public class ProductDetailActivity extends AppCompatActivity {
    String getProductById = new LocalNetwork().getUrl()+"/products/viewProduct/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        Product p = getIntent().getParcelableExtra("prod");
        String id = String.valueOf(p.getProductId());
        ImageView thumbnail = (ImageView) findViewById(R.id.product_pic);
        TextView name = (TextView) findViewById(R.id.name_prod);
        TextView manufacturer = findViewById(R.id.manufactur);
        TextView price = (TextView) findViewById(R.id.prod_price);
        TextView des = findViewById(R.id.description);
        RequestQueue q = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, getProductById + id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Glide.with(getApplicationContext()).load(Uri.parse(response.getString("image"))).into(thumbnail);
                    name.setText(response.getString("productName"));
                    manufacturer.setText(response.getString("manufacturer"));
                    price.setText(String.valueOf(response.getDouble("price")));
                    des.setText(response.getString("description"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        q.add(jor);
    }
}