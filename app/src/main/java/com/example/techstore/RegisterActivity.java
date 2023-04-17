package com.example.techstore;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class RegisterActivity extends AppCompatActivity {
    String regapi = new LocalNetwork().getUrl()+"/auth/register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        EditText name = (EditText) findViewById(R.id.fullname);
        EditText phonenumber = (EditText) findViewById(R.id.phonenumber);
        EditText email = (EditText) findViewById(R.id.editTextEmail1);
        EditText username = (EditText) findViewById(R.id.editTextUsername1);
        EditText password = (EditText) findViewById(R.id.editTextPassword1);
        EditText repass = findViewById(R.id.editRepass);
        TextView signin = findViewById(R.id.textViewSignIn1);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.buttonRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fulname = name.getText().toString();
                String em = email.getText().toString();
                String phone = phonenumber.getText().toString();
                String pass = password.getText().toString();
                String us=username.getText().toString();
                String repas = repass.getText().toString();
                if (TextUtils.isEmpty(us)) {
                    username.setError("Username is required.");
                    return;
                }

                if (TextUtils.isEmpty(em)) {
                    email.setError("Email is required.");
                    return;
                }
                if (TextUtils.isEmpty(phone)) {
                    phonenumber.setError("Phone number is required.");
                    return;
                }
                if (TextUtils.isEmpty(pass)) {
                    password.setError("Password is required.");
                    return;
                }
                if (TextUtils.isEmpty(repas)) {
                    repass.setError("Re-password is required.");
                    return;
                }
                if(!pass.equals(repas)) {
                    Toast.makeText(getApplicationContext(),"repassword isn't correct" , Toast.LENGTH_SHORT).show();
                }
                else{
                    RequestQueue q = Volley.newRequestQueue(getApplicationContext());

                    try {
                        JSONObject jsonbody = new JSONObject();
                        jsonbody.put("name",fulname);
                        jsonbody.put("username",us);
                        jsonbody.put("phone_number",phone);
                        jsonbody.put("email",em);
                        jsonbody.put("password",pass);
                        final String jsb=jsonbody.toString();
                        StringRequest sr = new StringRequest(Request.Method.POST, regapi, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response.equalsIgnoreCase("Registered Successfully")) {
                                    Toast.makeText(getApplicationContext(), "Registered Successfully", Toast.LENGTH_SHORT).show();
                                    Intent i=new Intent(RegisterActivity.this,LoginActivity.class);
                                    startActivity(i);
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }){
                            @Override
                            public String getBodyContentType() {
                                return "application/json; charset=utf-8";
                            }
                            @Override
                            public byte[] getBody(){
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
            }
        });

    }
}