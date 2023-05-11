package com.example.techstore;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
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


public class OrderViewFragment extends Fragment {


    private OrderAdapter orderAdapter;
    private List<Orders> orderList = new ArrayList<>();
    public OrderViewFragment() {
        // Required empty public constructor
    }

    RequestQueue q ;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        q = Volley.newRequestQueue(getContext());
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_view, container, false);
        recyclerView = view.findViewById(R.id.order_history_recycler_view);
        orderAdapter = new OrderAdapter();
//        SwipeRefreshLayout swipeRefreshLayout = getActivity().findViewById(R.id.swipeRefreshLayout2);
//        swipeRefreshLayout.setEnabled(false);
        getOrderList();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(orderAdapter);

        return view;
    }
    private void getOrderList() {
        // Call API to get order list
        String url = new LocalNetwork().getUrl()+"/orders/viewOrderByUser/" + StaticConfig.UID;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d(TAG, "Response: " + response.toString());

                    try {

                        for(int i = 0;i< response.length();i++){
                            Orders o = new Orders();
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
                            o.setOrderItems(oit);
                            orderList.add(o);
                        }
                        orderAdapter.setOrdersList(orderList);

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }


                },
                error -> {
                    Log.e(TAG, "Error occurred while getting order list: " + error.getMessage());
                    Toast.makeText(getContext(), "Error occurred while getting order list", Toast.LENGTH_SHORT).show();
                });
        q.add(request);
    }
}