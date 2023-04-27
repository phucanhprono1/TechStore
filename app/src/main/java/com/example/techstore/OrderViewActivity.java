package com.example.techstore;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.techstore.adapter.OrderAdapter;
import com.example.techstore.config.StaticConfig;
import com.example.techstore.models.OrderItem;
import com.example.techstore.models.Orders;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrderViewActivity extends AppCompatActivity {
    private OrderAdapter orderAdapter;
    private List<Orders> orderList;
    RequestQueue q ;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_view);
        recyclerView = findViewById(R.id.order_history_recycler_view1);
        orderAdapter = new OrderAdapter();
        getOrderList();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(orderAdapter);
    }
    private void getOrderList() {
        // Call API to get order list
        String url = new LocalNetwork().getUrl()+"/orders/viewOrderByUser/" + StaticConfig.UID;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d(TAG, "Response: " + response.toString());
                    Orders o = new Orders();
                    try {
                        for(int i = 0;i< response.length();i++){

                            JSONObject re = response.getJSONObject(i);
                            o.setOrderId(re.getInt("orderId"));
                            o.setDate((String) re.getString("date"));
                            o.setOrderStatus(re.getString("orderStatus"));
                            o.setLocation(re.getString("location"));
                            o.setPaymentMethod(re.getString("paymentMethod"));
                            o.setTotal_price((float) re.getDouble("total_price"));
                            JSONArray jsOit = re.getJSONArray("orderItems");
                            List<OrderItem> oit = new ArrayList<>();
                            for(int j = 0;j<jsOit.length();j++){
                                JSONObject re2 = jsOit.getJSONObject(j);

                                Gson gson = new Gson();
                                OrderItem oi = gson.fromJson(re2.toString(), new TypeToken<OrderItem>(){}.getType());
                                oit.add(oi);
                            }
                        }

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }


                },
                error -> {
                    Log.e(TAG, "Error occurred while getting order list: " + error.getMessage());
                    Toast.makeText(getApplicationContext(), "Error occurred while getting order list", Toast.LENGTH_SHORT).show();
                });
        q.add(request);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        i.putExtra("key",StaticConfig.CURRENT_KEY);
        startActivity(i);
        finish();
    }
}