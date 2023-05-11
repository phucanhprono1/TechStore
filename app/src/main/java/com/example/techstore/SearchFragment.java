package com.example.techstore;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.techstore.adapter.ProductAdapter;
import com.example.techstore.models.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment implements ProductAdapter.ProductClickListener {
    private EditText etSearch;
    private ImageButton btnSearch;
    private TextView tvResult;
    private RecyclerView rvProducts;
    String searchUrl = new LocalNetwork().getUrl()+"/products/search";
    ProductAdapter productAdapter;
    List<Product> searchResult = new ArrayList<>();
    RequestQueue q;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);


        etSearch = view.findViewById(R.id.et_search);
        btnSearch = view.findViewById(R.id.btn_search);
        tvResult = view.findViewById(R.id.tv_result);
        rvProducts = view.findViewById(R.id.rv_products);

//        SwipeRefreshLayout swipeRefreshLayout = getActivity().findViewById(R.id.swipeRefreshLayout2);
//        swipeRefreshLayout.setEnabled(false);
        q = Volley.newRequestQueue(getContext());
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

        rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        productAdapter = new ProductAdapter(searchResult,this);
        rvProducts.setAdapter(productAdapter);
        return view;
    }

    @Override
    public void onItemClick(Product product) {
        Intent i1 = new Intent(getContext(),ProductDetailActivity.class);
        i1.putExtra("prod",product);
//        i1.putExtra("key1",key1);
//        i1.putExtra("id",uid);
        startActivity(i1);
    }
}