package com.example.michael.ezaytask;

import android.Manifest;
import android.content.pm.PackageManager;
import android.nfc.Tag;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.michael.ezaytask.model.Location;
import com.example.michael.ezaytask.model.Trip;
import com.google.android.gms.common.wrappers.PackageManagerWrapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String BASE_URL = "http://52.55.117.63:3000/trip/";
    private static final String TAG = "MainActivity";
    private static final int LOCATION_REQUEST = 500;

    static int id = 1;
    public Trip responseTrip;

    GoogleMap map;
    MarkerOptions place1, place2;
    Button first, prev, next, last;
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText sourceET = findViewById(R.id.sourceET);
        EditText destinationET = findViewById(R.id.destinationET);
        first = findViewById(R.id.firstBtn);
        prev = findViewById(R.id.prevtBtn);
        next = findViewById(R.id.nextBtn);
        last = findViewById(R.id.lastBtn);


        mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        EzayAPI ezayAPI = retrofit.create(EzayAPI.class);
        Call<Trip> call = ezayAPI.GetFirstTrip();

        call.enqueue(new Callback<Trip>() {
            @Override
            public void onResponse(Call<Trip> call, Response<Trip> response) {
                Log.d(TAG,"onResponse: Server Response: " + response.toString());
                Log.d(TAG,"onResponse: received information" + response.body().toString());
                responseTrip = response.body();

                LatLng sourceLocation = new LatLng(responseTrip.getSource().getLatitutde(),responseTrip.getSource().getLongtiude());
                map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_source))
                        .position(sourceLocation).title("Source"));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(sourceLocation, 16));

                LatLng destinationLocation = new LatLng(responseTrip.getDestination().getLatitutde(),responseTrip.getDestination().getLongtiude());
                map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_destination))
                        .position(destinationLocation).title("Destination"));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(destinationLocation, 16));
            }

            @Override
            public void onFailure(Call<Trip> call, Throwable t) {
                Log.e(TAG, "onFailure: Something was wrong: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }
}