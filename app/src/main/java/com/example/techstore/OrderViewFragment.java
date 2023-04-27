package com.example.techstore;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.techstore.models.Orders;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;


public class OrderViewFragment extends Fragment {


    private OrderAdapter orderAdapter;
    private List<Orders> orderList;
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
        getOrderList();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(orderAdapter);

        return view;
    }
    private void getOrderList() {
        // Call API to get order list
        String url = new LocalNetwork()+"/viewOrderByUser/" + StaticConfig.UID;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    Gson gson = new Gson();
                    orderList = gson.fromJson(response.toString(),new TypeToken<List<Orders>>() {}.getType());
                    orderAdapter.setOrdersList(orderList);
                },
                error -> {
                    Log.e(TAG, "Error occurred while getting order list: " + error.getMessage());
                    Toast.makeText(getContext(), "Error occurred while getting order list", Toast.LENGTH_SHORT).show();
                });
        q.add(request);
    }
}