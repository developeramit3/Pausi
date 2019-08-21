package com.t.pausi.Activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.t.pausi.R;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {
    ImageView location_back;
    BitmapDescriptor icon;
    GoogleMap mMap;
    String lat = "", lon = "";
    double P_latitude = 0.0, P_longitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        MapsInitializer.initialize(this);

        Bundle bundle = new Bundle();
        bundle = getIntent().getExtras();
        if (bundle != null) {
            lat = bundle.getString("lat");
            lon = bundle.getString("lon");
            P_latitude = Double.parseDouble(lat);
            P_longitude = Double.parseDouble(lon);
        }

        location_back = findViewById(R.id.location_back);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.location_map);
        mapFragment.getMapAsync(this);
        icon = BitmapDescriptorFactory.fromResource(R.drawable.prop_marker);

        location_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
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
        mMap.setMyLocationEnabled(false);
        LatLng latLng = new LatLng(P_latitude, P_longitude);
        MarkerOptions marker = new MarkerOptions().position(latLng).flat(true).anchor(0.5f, 0.5f).icon(icon);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);
        mMap.animateCamera(cameraUpdate);
        mMap.addMarker(marker);

    }
}
