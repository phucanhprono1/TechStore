package com.example.techstore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.techstore.adapter.ItemCartAdapter;
import com.example.techstore.config.StaticConfig;
import com.example.techstore.models.Cart;
import com.example.techstore.models.CartItem;
import com.example.techstore.models.Product;
import com.google.gson.Gson;
//import com.example.techstore.retrofit.CartAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
//import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CartActivity extends AppCompatActivity implements ItemCartAdapter.AddMoreClickListener, ItemCartAdapter.SubtractClickListener{
    String getCartByCustomerId = new LocalNetwork().getUrl()+"/customer/cart/view/";

    String key;
    private String userId;
    ItemCartAdapter itemCartAdapter;
    int uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Bundle b = getIntent().getExtras();
        key = b.getString("key");
        uid = b.getInt("id");
        String key1 = key;
//        Toast.makeText(getApplicationContext(),"key:"+key, Toast.LENGTH_SHORT).show();
        RecyclerView rcv = findViewById(R.id.cartItemsRecyclerView);

        userId = String.valueOf(uid);
        Toast.makeText(getApplicationContext(),"userId:"+ StaticConfig.UID, Toast.LENGTH_SHORT).show();


        RequestQueue q = Volley.newRequestQueue(this);
        List<CartItem> itc = new ArrayList<>();
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(new LocalNetwork().getUrl())
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        CartAPI cartAPI = retrofit.create(CartAPI.class);
//        Call<List<CartItem>> call = cartAPI.getCartByCustomerId(StaticConfig.UID);
//
//        call.enqueue(new Callback<List<CartItem>>() {
//
//
//            @Override
//            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
//                List<CartItem> cartItems = response.body();
//
//                for (CartItem cartItem : cartItems) {
//                    CartItem c = new CartItem();
//
//                    c.setId(cartItem.getId());
//                    c.getProduct().setProductName(cartItem.getProduct().getProductName());
//                    c.getProduct().setPrice(cartItem.getProduct().getPrice());
//                    c.getProduct().setProductId(cartItem.getProduct().getProductId());
//                    c.setQuantity(cartItem.getQuantity());
//                    itc.add(c);
//
//                }
//                itemCartAdapter = new ItemCartAdapter(itc, CartActivity.this, CartActivity.this);
//            }
//
//            @Override
//            public void onFailure(Call<List<CartItem>> call, Throwable t) {
//
//            }
//        });
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, getCartByCustomerId + StaticConfig.UID, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                CartItem itemCart = new CartItem();
                try {
//                    itemCart.setId(response.getInt("cartId"));
//                    itemCart.setQuantity(response.getInt("product_quantity"));
                    JSONArray jsonArray = response.getJSONArray("cartItems");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject item = jsonArray.getJSONObject(i);
                        itemCart.setId(item.getInt("id"));
                        Product p = new Product() ;

                        JSONObject prod =item.optJSONObject("product");
                        Log.d("product", prod.toString());
//                        Gson gson = new Gson();
//                        p = gson.fromJson(prod.toString(), Product.class);
                        if(prod != null){
                            Log.d("product", prod.getInt("productId")+"");
                            Log.d("product", prod.getString("productName")+"");
                            Log.d("product", prod.getDouble("price")+"");
                            Log.d("product", prod.getString("image"));
                            p.setProductId(prod.getInt("productId"));
                            p.setProductName(prod.getString("productName"));
                            p.setPrice((float) prod.getDouble("price"));
                            p.setImage(prod.getString("image"));
                        }
//                        else{
//                            p.setProductId(1);
//                            p.setProductName("lỗi gì v ba");
//                            p.setPrice(10000.0f);
//                            p.setImage("https://firebasestorage.googleapis.com/v0/b/techecommerceserver.appspot.com/o/products_img%2Fsamsung_s23.png?alt=media&token=96469d6a-7f70-465d-979b-69bdd54cdc7d");
//                        }
                        itemCart.setProduct(p);
                        itemCart.setQuantity(item.getInt("quantity"));



                        itc.add(itemCart);
                        itemCartAdapter.setItemCart(itc);
                    }
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
        itemCartAdapter = new ItemCartAdapter(itc, this,this);
        rcv.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        rcv.setAdapter(itemCartAdapter);
    }

    @Override
    public void onAddMoreClick(CartItem product) {

    }

    @Override
    public void onSubtractClick(CartItem product) {

    }
}