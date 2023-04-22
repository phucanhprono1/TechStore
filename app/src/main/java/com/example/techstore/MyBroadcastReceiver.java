package com.example.techstore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SHUTDOWN)) {
            final PendingResult pendingResult = goAsync();
            Thread thread = new Thread() {
                public void run() {
                    // Khởi tạo request
                    String key = null;
                    String url = new LocalNetwork().getUrl()+"/auth/logout";
                    RequestQueue q = Volley.newRequestQueue(context);
                    try {
                        JSONObject jsonbody = new JSONObject();
                        jsonbody.put("role","customer");
                        jsonbody.put("key",key);
                        String requestBody = jsonbody.toString();
                        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                pendingResult.finish();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }){
                            @Override
                            public HashMap<String, String> getParams() {
                                // Thêm các tham số cho POST request
                                HashMap<String, String> params = new HashMap<>();
                                params.put("role", "customer");
                                params.put("key", key);
                                return params;
                            }
                        };
                        q.add(sr);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            thread.start();
        }
    }
}