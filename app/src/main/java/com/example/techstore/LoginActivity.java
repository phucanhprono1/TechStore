package com.example.techstore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.techstore.config.StaticConfig;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    LocalNetwork lc = new LocalNetwork();
    String api = lc.getUrl()+"/auth/login";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EditText mUsername = (EditText) findViewById(R.id.editTextUsername);
        EditText mPasswordField = findViewById(R.id.editTextPassword);
        sharedPreferences = getSharedPreferences("dataLogin",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Button btnLogin = (Button) findViewById(R.id.buttonSignIn);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = mUsername.getText().toString();
                String password = mPasswordField.getText().toString();
                if (TextUtils.isEmpty(username)) {
                    mUsername.setError("Email is required.");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    mPasswordField.setError("Password is required.");
                    return;
                }

                try {
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    final JSONObject jsonbody = new JSONObject();
                    jsonbody.put("username",username);
                    jsonbody.put("password",password);
                    jsonbody.put("role","customer");

                    JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, api,jsonbody, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                if(response.getString("response").equalsIgnoreCase("Login Successfully!")){
                                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                    i.putExtra("username",response.getString("username"));
                                    StaticConfig.UID = response.getString("id");

                                    editor.putString("key",response.getString("key"));
                                    editor.putString("username",response.getString("username"));
                                    editor.putString("id",response.getString("id"));
                                    editor.apply();
//                                    StaticConfig.CURRENT_KEY = response.getString("key");
                                    i.putExtra("key",response.getString("key"));
                                    i.putExtra("id",response.getString("id"));
                                    startActivity(i);
                                }
                                Toast.makeText(getApplicationContext(),response.getString("response"),Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("VOLLEY", error.toString());
                        }
                    }){
                        @Override
                        public String getBodyContentType() {
                            return "application/json; charset=utf-8";
                        }
//                        @Override
//                        public byte[] getBody() {
//                            try {
//                                return jsonbody.toString().getBytes("utf-8");
//                            } catch (UnsupportedEncodingException e) {
//                                throw new RuntimeException(e);
//                            }
//                        }
                    };
                    queue.add(objectRequest);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        findViewById(R.id.textViewRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });
    }
}