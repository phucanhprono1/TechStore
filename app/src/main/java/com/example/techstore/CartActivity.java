package com.example.techstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.techstore.adapter.ItemCartAdapter;
import com.example.techstore.models.ItemCart;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity implements ItemCartAdapter.AddMoreClickListener, ItemCartAdapter.SubtractClickListener{
    String getCartByCustomerId = new LocalNetwork().getUrl()+"/customer/cart/view/";
    RequestQueue q ;
    String key;
    private String userId;
    ItemCartAdapter itemCartAdapter;
    int uid;

    DatabaseReference userReference = FirebaseDatabase.getInstance("https://techecommerceserver-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("currentUser");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Bundle b = getIntent().getExtras();
        key = b.getString("key");
        uid = b.getInt("id");
        String key1 = key;
        Toast.makeText(getApplicationContext(),"key:"+key, Toast.LENGTH_SHORT).show();
        RecyclerView rcv = findViewById(R.id.cartItemsRecyclerView);

//        List<Integer> i2 = new ArrayList<>();
//        userReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Integer i = snapshot.child(key1).getValue(Integer.class);
//                i2.add(i);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
        userId = String.valueOf(uid);

        q = Volley.newRequestQueue(getApplicationContext());
        List<ItemCart> itc = new ArrayList<>();
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, getCartByCustomerId+userId, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ItemCart itemCart = new ItemCart();
                try {
                    JSONArray jsonArray = response.getJSONArray("cartItems");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject item = jsonArray.getJSONObject(i);
                        JSONObject product =item.getJSONObject("product");

                        int productId = product.getInt("productId");
                        itemCart.getProduct().setProductId(productId);
                        itemCart.getProduct().setProductName(product.getString("productName"));
                        itemCart.getProduct().setPrice((float) product.getDouble("price"));
                        itemCart.getProduct().setImage(product.getString("image"));
                        itemCart.setQuantity(product.getInt("quantity"));
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
    public void onAddMoreClick(ItemCart product) {
        RequestQueue que = Volley.newRequestQueue(getBaseContext());
    }

    @Override
    public void onSubtractClick(ItemCart product) {

    }
}