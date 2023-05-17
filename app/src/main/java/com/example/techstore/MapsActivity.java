package com.example.techstore;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.techstore.databinding.ActivityMapsBinding;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private String selectedPlaceName;
    private LatLng selectedPlaceLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Places.initialize(getApplicationContext(), "AIzaSyA5Pe7qxMyfttMnx5ev4ksVhxap4yeUsm8");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
    public void onSearchButtonClick(View view) {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .setTypeFilter(TypeFilter.ADDRESS)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Lấy thông tin vị trí đã chọn từ intent
                Place place = Autocomplete.getPlaceFromIntent(data);
                selectedPlaceName = place.getName();
                selectedPlaceLatLng = place.getLatLng();

                // Thêm marker vào bản đồ cho vị trí đã chọn
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(selectedPlaceLatLng).title(selectedPlaceName));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(selectedPlaceLatLng));
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // Xử lý lỗi
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.d("TAG", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // Xử lý khi người dùng huỷ tìm kiếm
            }
        }
    }
    public void onNavigateButtonClick(View view) {
        if (selectedPlaceLatLng != null) {
            String mapUrl = "https://www.google.com/maps/place/" +
                    selectedPlaceLatLng.latitude + "," + selectedPlaceLatLng.longitude;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl));
            startActivity(intent);
        }
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}