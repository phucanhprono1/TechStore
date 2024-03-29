package com.example.techstore;



import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.techstore.adapter.ItemCartAdapter;
import com.example.techstore.config.StaticConfig;
import com.example.techstore.models.Cart;
import com.example.techstore.models.CartItem;
import com.example.techstore.models.Product;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
//import com.example.techstore.retrofit.CartAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import retrofit2.Call;
import retrofit2.Callback;
//import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CartActivity extends AppCompatActivity implements ItemCartAdapter.AddMoreClickListener, ItemCartAdapter.SubtractClickListener, ItemCartAdapter.RemoveClickListener {
    String getCartByCustomerId = new LocalNetwork().getUrl() + "/customer/cart/view/";
    String createOrder = new LocalNetwork().getUrl() + "/orders/add";
    String removealll = new LocalNetwork().getUrl()+"/customer/cart/remove/";

    String key;
    private String userId;
    ItemCartAdapter itemCartAdapter;
    int uid;
    SwipeRefreshLayout swipeRefreshLayout;
    EditText addressEdt;
    EditText cityEdt;
    Button order,removeall;

    List<CartItem> itc = new ArrayList<>();
    TextView itemTotal, totalFee;
    ImageButton map;
    MapsFragment mapFragment;
    private PlacesClient placesClient;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    Spinner spinner;
    private ArrayAdapter<String> paymentAdapter;
    TextView location;
    RequestQueue q;
    private static final String PAYPAL_CLIENT_ID = "AUgQToK94OvleJVGAe5lXIsmVirv8vmpM21TVyNus7r-D3mdMk0ypCIMB8u-kriEWMc4wIDvwzMhkGuS";
    private static final int REQUEST_CODE_PAYPAL_PAYMENT = 1;
    static String accessToken;

    private PayPalConfiguration paypalConfiguration = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(PAYPAL_CLIENT_ID);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Bundle b = getIntent().getExtras();
        key = b.getString("key");
        uid = b.getInt("id");
        String key1 = key;

        location = findViewById(R.id.tvLocation);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(CartActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    return;
                }


                FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(CartActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            StaticConfig.CURRENT_LOCATION = "https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude;
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(StaticConfig.CURRENT_LOCATION));
                            startActivity(intent);
                        }
                    }
                });
                Toast.makeText(CartActivity.this, "Đã lấy vị trí hiện tại"+StaticConfig.CURRENT_LOCATION, Toast.LENGTH_SHORT).show();
            }
        });
        addressEdt = findViewById(R.id.inputAddress);
        cityEdt = findViewById(R.id.inputCity);
        order = findViewById(R.id.buttonPlaceYourOrder);
        itemTotal = findViewById(R.id.tvitemtotalAmount);
        totalFee = findViewById(R.id.tvTotalAmount);
        map = findViewById(R.id.mapFrag);

        spinner=findViewById(R.id.paymentMethodSpinner);
        paymentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"COD", "Zalo Pay", "Paypal"});
        paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(paymentAdapter);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);



        removeall = findViewById(R.id.btnRemoveAll);

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshCartItems();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        RecyclerView rcv = findViewById(R.id.cartItemsRecyclerView);
        getAccessToken();
        userId = String.valueOf(uid);

        q = Volley.newRequestQueue(getApplicationContext());
        removeall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringRequest stringRequest = new StringRequest(Request.Method.DELETE, removealll + StaticConfig.UID, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(CartActivity.this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                        refreshCartItems();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CartActivity.this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
                q.add(stringRequest);
            }
        });
//List<CartItem> itc = new ArrayList<>();
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, getCartByCustomerId + StaticConfig.UID, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    itemTotal.setText(response.getInt("product_quantity") + "");
                    totalFee.setText(response.getDouble("total_price") + "");
                    StaticConfig.TOTAL = String.valueOf(response.getDouble("total_price"));
                    JSONArray jsonArray = response.getJSONArray("cartItems");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject item = jsonArray.getJSONObject(i);
                        CartItem itemCart = new CartItem();
                        itemCart.setId(item.getInt("id"));
                        Product p = new Product();

                        JSONObject prod = item.optJSONObject("product");
                        Log.d("product", prod.toString());


                        Log.d("product", prod.getInt("productId") + "");
                        Log.d("product", prod.getString("productName") + "");
                        Log.d("product", prod.getDouble("price") + "");
                        Log.d("product", prod.getString("image"));
                        p.setProductId(prod.getInt("productId"));
                        p.setProductName(prod.getString("productName"));
                        p.setPrice((float) prod.getDouble("price"));
                        p.setImage(prod.getString("image"));

                        itemCart.setProduct(p);
                        itemCart.setQuantity(item.getInt("quantity"));
                        itc.add(itemCart);
                    }
                    itemCartAdapter.setItemCart(itc);
                    itemCartAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        q.add(jor);
        itemCartAdapter = new ItemCartAdapter(itc, this, this,this);
        rcv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rcv.setAdapter(itemCartAdapter);
//        StaticConfig.ADDRESS = addressEdt.getText().toString();
//        StaticConfig.CITY = cityEdt.getText().toString();
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticConfig.ADDRESS = addressEdt.getText().toString();
                StaticConfig.CITY = cityEdt.getText().toString();
                String paymentMethod = spinner.getSelectedItem().toString();
                if (StaticConfig.ADDRESS.isEmpty() || StaticConfig.CITY.isEmpty()) {
                    Toast.makeText(CartActivity.this, "Please enter your address and city", Toast.LENGTH_SHORT).show();
                } else {
                    if(paymentMethod.equals("Paypal")){
                        createOrder();
                    }
                    else{
                        JSONObject jsonObject = new JSONObject();
                        try {
                            JSONObject addressObj = new JSONObject();
                            addressObj.put("address", StaticConfig.ADDRESS);
                            addressObj.put("city", StaticConfig.CITY);
                            jsonObject.put("address", addressObj);
                            jsonObject.put("location", StaticConfig.CURRENT_LOCATION);
                            jsonObject.put("paymentMethod", paymentMethod);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JsonObjectRequest jor1 = new JsonObjectRequest(Request.Method.POST, createOrder + "/" + StaticConfig.UID, jsonObject, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Toast.makeText(CartActivity.this, "Order successfully", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(CartActivity.this, MainActivity.class);
                                intent.putExtra("key", StaticConfig.CURRENT_KEY);
                                startActivity(intent);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(CartActivity.this, "Order failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                        q.add(jor1);
                    }

                }
            }
        });
    }
    String url = "https://api-m.sandbox.paypal.com";

    private void createOrder() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Accept", "application/json");
        client.addHeader("Content-type", "application/json");
        client.addHeader("Authorization", "Bearer " + accessToken);

        String order = "{"
                + "\"intent\": \"CAPTURE\","
                + "\"purchase_units\": [\n" +
                "      {\n" +
                "        \"amount\": {\n" +
                "          \"currency_code\": \"USD\",\n" +
                "          \"value\": \""+totalFee.getText().toString()+"\"\n" +
                "        }\n" +
                "      }\n" +
                "    ],\"application_context\": {\n" +
                "        \"brand_name\": \"TechStore\",\n" +
                "        \"return_url\": \"techstorepay://example.com\",\n" +
                "        \"cancel_url\": \"techstorepay://example.com\"\n" +
                "    }}";
        HttpEntity entity = new StringEntity(order, "utf-8");

        client.post(this, url+"/v2/checkout/orders", entity, "application/json",new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("RESPONSE", responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.i("RESPONSE", responseString);
                try {
                    JSONArray links = new JSONObject(responseString).getJSONArray("links");

                    //iterate the array to get the approval link
                    for (int i = 0; i < links.length(); ++i) {
                        JSONObject linkObj = links.getJSONObject(i);
                        String rel = links.getJSONObject(i).getString("rel");
                        if (rel.equals("approve")){
                            String link = linkObj.getString("href");
                            //redirect to this link via CCT
                            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                            CustomTabsIntent customTabsIntent = builder.build();
                            customTabsIntent.launchUrl(CartActivity.this, Uri.parse(link));
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }


    // Kiểm tra quyền truy cập vị trí
    String encodeStringToBase64(){
        String input = "AUgQToK94OvleJVGAe5lXIsmVirv8vmpM21TVyNus7r-D3mdMk0ypCIMB8u-kriEWMc4wIDvwzMhkGuS:EGt8zwbVkGepacWetnbRad7gJL-dydvEa8t0Ah4sZ4oe8X4FbOdtDoqP2y5DPTEgNGaw3Oe27z2SeEJ6";
        String encodedString = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            encodedString = Base64.getEncoder().encodeToString(input.getBytes());
        }
        return encodedString;
    }

    void getAccessToken(){
        String AUTH = encodeStringToBase64();
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Accept", "application/json");
        client.addHeader("Content-type", "application/x-www-form-urlencoded");
        client.addHeader("Authorization", "Basic "+ AUTH);
        String jsonString = "grant_type=client_credentials";

        HttpEntity entity = new StringEntity(jsonString, "utf-8");

        client.post(this, "https://api-m.sandbox.paypal.com/v1/oauth2/token", entity, "application/x-www-form-urlencoded",new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, String response) {
                try {
                    JSONObject jobj = new JSONObject(response);
                    accessToken = jobj.getString("access_token");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        });
    }
    public static String getMyAccessToken(){
        return accessToken;
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }
    

    @Override
    public void onAddMoreClick(CartItem product) {
        refreshCartItems();
    }

    @Override
    public void onSubtractClick(CartItem product) {
        refreshCartItems();
    }

    private void refreshCartItems() {
        RequestQueue q = Volley.newRequestQueue(this);
        itc.clear();
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, getCartByCustomerId + StaticConfig.UID, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                try {
                    itemTotal.setText(response.getInt("product_quantity") + "");
                    totalFee.setText(response.getDouble("total_price") + "");
                    JSONArray jsonArray = response.getJSONArray("cartItems");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        CartItem itemCart = new CartItem();
                        JSONObject item = jsonArray.getJSONObject(i);
                        itemCart.setId(item.getInt("id"));
                        Product p = new Product();

                        JSONObject prod = item.optJSONObject("product");
                        Log.d("product", prod.toString());


                        Log.d("product", prod.getInt("productId") + "");
                        Log.d("product", prod.getString("productName") + "");
                        Log.d("product", prod.getDouble("price") + "");
                        Log.d("product", prod.getString("image"));
                        p.setProductId(prod.getInt("productId"));
                        p.setProductName(prod.getString("productName"));
                        p.setPrice((float) prod.getDouble("price"));
                        p.setImage(prod.getString("image"));

                        itemCart.setProduct(p);
                        itemCart.setQuantity(item.getInt("quantity"));
                        itc.add(itemCart);
                    }
                    itemTotal.setText(response.getInt("product_quantity") + "");
                    totalFee.setText(response.getDouble("total_price") + "");
                    itemCartAdapter.setItemCart(itc);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        q.add(jor);
    }



    @Override
    public void onRemoveClick(CartItem product) {

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PAYPAL_PAYMENT) {
            if (resultCode == RESULT_OK) {
                // The payment was completed successfully, do your post-payment processing here.

            } else if (resultCode == RESULT_CANCELED) {
                // The payment was canceled, handle this as needed.
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                // An invalid payment or PayPalConfiguration was submitted, handle this as needed.
            }
        }
    }
}