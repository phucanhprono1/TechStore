package com.example.techstore;

import static android.content.ContentValues.TAG;

import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.android.volley.toolbox.Volley;
import com.example.techstore.adapter.ItemCartAdapter;
import com.example.techstore.config.StaticConfig;
import com.example.techstore.models.Cart;
import com.example.techstore.models.CartItem;
import com.example.techstore.models.Product;
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
//import com.example.techstore.retrofit.CartAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
//import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CartActivity extends AppCompatActivity implements ItemCartAdapter.AddMoreClickListener, ItemCartAdapter.SubtractClickListener {
    String getCartByCustomerId = new LocalNetwork().getUrl() + "/customer/cart/view/";
    String createOrder = new LocalNetwork().getUrl() + "/orders/add";

    String key;
    private String userId;
    ItemCartAdapter itemCartAdapter;
    int uid;
    SwipeRefreshLayout swipeRefreshLayout;
    EditText addressEdt;
    EditText cityEdt;
    Button order;

    List<CartItem> itc = new ArrayList<>();
    TextView itemTotal, totalFee;
    ImageButton map;
    MapsFragment mapFragment;
    private PlacesClient placesClient;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    Spinner spinner;
    private ArrayAdapter<String> paymentAdapter;
    TextView location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Bundle b = getIntent().getExtras();
        key = b.getString("key");
        uid = b.getInt("id");
        String key1 = key;
//        mLocationPermissionGranted = checkPermission();
//
//        // Yêu cầu quyền truy cập vị trí
//        if (!mLocationPermissionGranted) {
//            requestPermission();
//        }
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
        paymentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"COD", "Zalo Pay", "ATM"});
        paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(paymentAdapter);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);


//        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
//                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
//
//        // Đặt danh sách các trường thông tin của địa điểm cần trả về
//        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
//
//        // Đăng ký lắng nghe sự kiện khi chọn địa điểm từ AutocompleteSupportFragment
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//                // Xử lý địa điểm đã chọn
//            }
//
//            @Override
//            public void onError(Status status) {
//                // Xử lý lỗi
//            }
//        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeRefreshLayout.setVisibility(View.GONE);
                // Launch the MapFragment
                MapsFragment mapsFragment = new MapsFragment();
                //Lấy FragmentManager của Activity hiện tại
                FragmentManager fragmentManager = getSupportFragmentManager();
                //Bắt đầu một FragmentTransaction mới
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                //Thay thế Fragment hiện tại (CartFragment) bằng MapFragment
                transaction.replace(R.id.container, mapsFragment);
                //Thêm transaction vào BackStack, để khi nhấn nút back trên điện thoại thì quay lại CartFragment
                transaction.addToBackStack(null);
                //Submit transaction
                transaction.commit();
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

        userId = String.valueOf(uid);

        RequestQueue q = Volley.newRequestQueue(this);
//List<CartItem> itc = new ArrayList<>();
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, getCartByCustomerId + StaticConfig.UID, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    itemTotal.setText(response.getInt("product_quantity") + "");
                    totalFee.setText(response.getDouble("total_price") + "");

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
        itemCartAdapter = new ItemCartAdapter(itc, this, this);
        rcv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rcv.setAdapter(itemCartAdapter);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = addressEdt.getText().toString();
                String city = cityEdt.getText().toString();
                String paymentMethod = spinner.getSelectedItem().toString();
                if (address.isEmpty() || city.isEmpty()) {
                    Toast.makeText(CartActivity.this, "Please enter your address and city", Toast.LENGTH_SHORT).show();
                } else {

                    JSONObject jsonObject = new JSONObject();
                    try {
                        JSONObject addressObj = new JSONObject();
                        addressObj.put("address", address);
                        addressObj.put("city", city);
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
        });
    }


    // Kiểm tra quyền truy cập vị trí
    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }

    //
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
//            if (grantResults.length > 0
//                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                mLocationPermissionGranted = true;
//                getCurrentLocation();
//            }
//        }
//    }

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

    private void getCurrentLocation() {
        if (mLocationPermissionGranted) {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                // Lấy link Google Map vị trí hiện tại
                StaticConfig.CURRENT_LOCATION = "http://maps.google.com/maps?q=" + latitude + "," + longitude;
                Toast.makeText(this, "Location: " + StaticConfig.CURRENT_LOCATION, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "getCurrentLocation: url = " + StaticConfig.CURRENT_LOCATION);
            } else {
                // Nếu không thể lấy vị trí hiện tại
                Log.d(TAG, "getCurrentLocation: location is null");
            }
        }
    }


}