package com.example.techstore;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.techstore.models.CurrentCustomerDTO;
import com.example.techstore.models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {
    String getProductById = new LocalNetwork().getUrl()+"/products/viewProduct/";
    String cartapi = new LocalNetwork().getUrl()+"/customer/cart/add";
    String currentuser = new LocalNetwork().getUrl()+"/auth/currentUser";
    CurrentCustomerDTO currentCustomerDTO;
    String key;

    List<Integer> i2 = new ArrayList<>();
    int uid;
    DatabaseReference userReference = FirebaseDatabase.getInstance("https://techecommerceserver-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("currentUser");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        Product p = getIntent().getParcelableExtra("prod");
        Bundle b = new Bundle();
        String key1 = (String) getIntent().getExtras().get("key1");
        uid = getIntent().getExtras().getInt("id");
        key = key1;
//        Toast.makeText(getApplicationContext(),"key:"+key+"key1"+key1,Toast.LENGTH_SHORT).show();
//        currentCustomerDTO = getIntent().getParcelableExtra("id");
//        String uid = String.valueOf(currentCustomerDTO.getId());
//        int uid = Integer.parseInt(userId);
        String id = String.valueOf(p.getProductId());
        ImageView thumbnail = (ImageView) findViewById(R.id.product_pic);
        TextView name = (TextView) findViewById(R.id.name_prod);
        TextView manufacturer = findViewById(R.id.manufactur);
        TextView price = (TextView) findViewById(R.id.prod_price);
        TextView des = findViewById(R.id.description);

        RequestQueue q = Volley.newRequestQueue(getApplicationContext());


        String userId = String.valueOf(uid);
        Toast.makeText(getApplicationContext(),"uid:"+userId,Toast.LENGTH_SHORT).show();
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, getProductById + id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Glide.with(getApplicationContext()).load(Uri.parse(response.getString("image"))).into(thumbnail);
                    name.setText(response.getString("productName"));
                    manufacturer.setText(response.getString("manufacturer"));
                    double pr = response.getDouble("price");
                    float fl = (float)pr;
                    String formattedNumber = String.format("%.1f",fl);
                    price.setText(formattedNumber);
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
        EditText numb = findViewById(R.id.number_input1);

        ImageButton addcart = findViewById(R.id.add_to_cart);
        addcart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String quant = numb.getText().toString();
                JsonObjectRequest jor1 = new JsonObjectRequest(Request.Method.POST, cartapi+"/"+userId+"/"+id+"/"+quant, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Intent i = new Intent(ProductDetailActivity.this,CartActivity.class);
                        try {
                            i.putExtra("caid",response.getString("cartId"));
//                            JSONArray jar = new JSONArray(response.getJSONArray("cartItems"));
//                            for(int i1 = 0; i1 < jar.length(); i1++){
//                                JSONObject jor = jar.getJSONObject(i1);
//                                i.putExtra("pid"+i1,jor.getString("productId"));
//                                i.putExtra("quan"+i1,jor.getString("quantity"));
//                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        i.putExtra("key",key);
                        i.putExtra("id",uid);
                        startActivity(i);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
//                    @Override
//                    public HashMap<String, String> getParams() {
//                        // Thêm các tham số cho POST request
//                        HashMap<String, String> params = new HashMap<>();
//                        params.put("customerId", userId);
//                        params.put("productId", id);
//                        params.put("quantity",quant);
//                        return params;
//                    }
                };
                q.add(jor1);
            }
        });


    }
}