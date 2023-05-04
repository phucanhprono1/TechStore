package com.example.techstore;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.techstore.config.StaticConfig;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.UUID;

public class LoginOptionActivity extends AppCompatActivity {

//    String regapi = new LocalNetwork().getUrl()+"/auth/register";
    String logapi = new LocalNetwork().getUrl()+"/auth/loginfb";
    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_option);
        FacebookSdk.sdkInitialize(getApplicationContext());
        
        Button signinacc = findViewById(R.id.sign_in_account);
        signinacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginOptionActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
        mAuth = FirebaseAuth.getInstance();
        LoginButton loginButton = findViewById(R.id.button_sign_in_fb);
        mCallbackManager = CallbackManager.Factory.create();
//        LoginManager.getInstance().signInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().logOut();
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }

        });
    }
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginOptionActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }
    private void updateUI(FirebaseUser user) {
//        Intent intent = new Intent( LoginOptionActivity.this, MainActivity.class);

        RequestQueue q = Volley.newRequestQueue(getApplicationContext());
//        String password=user.getUid();
        try {

            JSONObject jsonbody = new JSONObject();
            jsonbody.put("name",user.getDisplayName());
            jsonbody.put("email",user.getEmail());
            jsonbody.put("phone_number",user.getPhoneNumber()+"0123456789");
            jsonbody.put("username", user.getUid());
            jsonbody.put("password", user.getUid());
            jsonbody.put("role","customer");
            final String jsb=jsonbody.toString();
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, logapi,jsonbody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        if(response.getString("response").equalsIgnoreCase("Login Successfully!")){
                            Intent i = new Intent(LoginOptionActivity.this, MainActivity.class);
                            i.putExtra("username",response.getString("username"));
                            StaticConfig.UID = response.getString("id");
//                            StaticConfig.CURRENT_KEY = response.getString("key");
                            i.putExtra("key",response.getString("key"));
                            i.putExtra("id",response.getInt("id"));
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
                    Toast.makeText(getApplicationContext(),"User logged-in",Toast.LENGTH_SHORT).show();
                    Log.e("VOLLEY", error.toString());
                }
            }){
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

            };
            q.add(objectRequest);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}