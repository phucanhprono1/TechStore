package com.example.techstore;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.techstore.config.StaticConfig;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class CheckoutActivity extends AppCompatActivity {
    TextView orderID_label;
    Button confirm_btn;
    String orderID;
    TextView price_label;
    String createOrder = new LocalNetwork().getUrl() + "/orders/paypal";
    RequestQueue q;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        //get the orderID from the query parameter
        Uri redirectUri = getIntent().getData();
        orderID = redirectUri.getQueryParameter("token");

        //set the orderID string to the UI
        orderID_label = (TextView) findViewById(R.id.orderID);
        orderID_label.setText("Order ID: " +orderID);
        price_label = (TextView) findViewById(R.id.amt);
        price_label.setText("Total: $" + StaticConfig.TOTAL);
        //add an onClick listener to the confirm button
        confirm_btn = findViewById(R.id.confirm_btn);
        q = Volley.newRequestQueue(getApplicationContext());
        confirm_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                captureOrder(orderID); //function to finalize the payment
            }
        });
    }

    void captureOrder(String orderID){
        //get the accessToken from MainActivity
        String accessToken = CartActivity.getMyAccessToken();

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Accept", "application/json");
        client.addHeader("Content-type", "application/json");
        client.addHeader("Authorization", "Bearer " + accessToken);

        client.post("https://api-m.sandbox.paypal.com/v2/checkout/orders/"+orderID+"/capture", new TextHttpResponseHandler() {


            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                Log.e("RESPONSE", response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                try {
                    JSONObject jobj = new JSONObject(response);
                    JSONObject jsonObject = new JSONObject();
                    try {
                        JSONObject addressObj = new JSONObject();
                        addressObj.put("address", StaticConfig.ADDRESS);
                        addressObj.put("city", StaticConfig.CITY);
                        jsonObject.put("address", addressObj);
                        jsonObject.put("location", StaticConfig.CURRENT_LOCATION);
                        jsonObject.put("paymentstatus", "Paid");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JsonObjectRequest jor1 = new JsonObjectRequest(Request.Method.POST, createOrder + "/" + StaticConfig.UID, jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(CheckoutActivity.this, "Order successfully", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
                            intent.putExtra("key", StaticConfig.CURRENT_KEY);
                            startActivity(intent);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(CheckoutActivity.this, "Order failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                    q.add(jor1);
//                    //redirect back to home page of app
//                    Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
//                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}