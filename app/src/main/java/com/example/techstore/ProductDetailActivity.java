package com.example.techstore;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.techstore.adapter.CommentAdapter;
import com.example.techstore.adapter.ProductAdapter;
import com.example.techstore.config.StaticConfig;
import com.example.techstore.models.Comment;
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {
    String getProductById = new LocalNetwork().getUrl()+"/products/viewProduct/";
    String getAllComment = new LocalNetwork().getUrl()+"/comments/viewComment/";

    String sendComment = new LocalNetwork().getUrl()+"/comments/addComment/10";

    String cartapi = new LocalNetwork().getUrl()+"/customer/cart/add";
    String currentuser = new LocalNetwork().getUrl()+"/auth/currentUser";
    CurrentCustomerDTO currentCustomerDTO;

    CommentAdapter commentA;
    String key;
    Comment c;
    Product p;
    List<Comment> commentList = new ArrayList<>();

    List<Integer> i2 = new ArrayList<>();
    int uid;
    CommentAdapter adapter;
    DatabaseReference userReference = FirebaseDatabase.getInstance("https://techecommerceserver-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("currentUser");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        EditText comment1 = (EditText) findViewById(R.id.comments);

        findViewById(R.id.send_comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment2 = comment1.getText().toString();
                String resp_comment = comment1.getText().toString();
                Integer rate = 1;
                RequestQueue q = Volley.newRequestQueue(getApplicationContext());

                try {
                    JSONObject jsonbody = new JSONObject();
                    jsonbody.put("comment", comment2);
                    jsonbody.put("rate", rate);
                    jsonbody.put("resp_comment", resp_comment);
                    jsonbody.put("product", 10);
                    jsonbody.put("customer", 2);
                    final String jsb = jsonbody.toString();
                    StringRequest sr = new StringRequest(Request.Method.POST, sendComment, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("response>>",response);
                            if (response.equalsIgnoreCase("Send Successfully")) {
                                Toast.makeText(getApplicationContext(), "Send Successfully", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(ProductDetailActivity.this, ProductDetailActivity.class);
                                startActivity(i);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("ERROR>>",error.toString());
                        }
                    }) {
                        @Override
                        public String getBodyContentType() {
                            return "application/json; charset=utf-8";
                        }

                        @Override
                        public byte[] getBody() {
                            try {
                                return jsb.getBytes("utf-8");
                            } catch (UnsupportedEncodingException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    };
                    q.add(sr);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });


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

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rvComments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommentAdapter(this, commentList);
        adapter.setClickListener(new CommentAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        });
        recyclerView.setAdapter(adapter);

        String userId = String.valueOf(uid);
        Toast.makeText(getApplicationContext(),"uid:"+userId,Toast.LENGTH_SHORT).show();
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, getProductById + id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("PRODUCT>>",response.getString("productName"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
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
        },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        q.add(jor);




//get comment
        JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, getAllComment+p.getProductId(),null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("COMMENT>>>",""+response.length());
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject catJson = response.getJSONObject(i);
                                Comment c = new Comment();
                                c.setComment_id(catJson.getInt("comment_id"));
                                c.setComment(catJson.getString("comment"));
                                c.setResp_comment(catJson.getString("resp_comment"));
                                c.setRate(catJson.getInt("rate"));
                                commentList.add(c);
                                //CommentAdapter.setComm(commentList);
                            }

                            catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        Log.d("commentList>>>",""+commentList.size());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                adapter.setComm(commentList);
                            }
                        });
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        q.add(stringRequest);

        EditText numb = findViewById(R.id.number_input1);

        ImageButton addcart = findViewById(R.id.add_to_cart);

        addcart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String quant = numb.getText().toString();
                JsonObjectRequest jor1 = new JsonObjectRequest(Request.Method.POST, cartapi+"/"+ StaticConfig.UID +"/"+id+"/"+quant, null, new Response.Listener<JSONObject>() {
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
                },

                        new Response.ErrorListener() {
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