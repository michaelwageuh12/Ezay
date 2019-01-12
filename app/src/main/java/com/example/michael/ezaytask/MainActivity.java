package com.example.michael.ezaytask;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.Routing;
import com.example.michael.ezaytask.model.Trip;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String BASE_URL = "http://52.55.117.63:3000/trip/";
    private static final String TAG = "MainActivity";

    int id = 1;
    public Trip responseTrip;

    GoogleMap map;
    Button first, prev, next, last;
    SupportMapFragment mapFragment;

    TextView sourceTV, destinationTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sourceTV = findViewById(R.id.sourceET);
        destinationTV = findViewById(R.id.destinationET);
        first = findViewById(R.id.firstBtn);
        prev = findViewById(R.id.prevtBtn);
        next = findViewById(R.id.nextBtn);
        last = findViewById(R.id.lastBtn);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id < 9)
                    id++;
                getTrip(false);
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id > 1)
                    id--;
                getTrip(false);
            }
        });

        first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = 1;
                getTrip(false);
            }
        });

        last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTrip(true);
            }
        });

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        getTrip(false);
    }

    void getTrip(boolean isLast) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        EzayAPI ezayAPI = retrofit.create(EzayAPI.class);
        Call<Trip> call = ezayAPI.GetTrip(id);

        if (isLast)
            call = ezayAPI.GetLatestTrip();

        map.clear();
        call.enqueue(new Callback<Trip>() {
            @Override
            public void onResponse(Call<Trip> call, Response<Trip> response) {
                Log.d(TAG, "onResponse: Server Response: " + response.toString());
                Log.d(TAG, "onResponse: received information" + response.body().toString());
                responseTrip = response.body();

                sourceTV.setText(responseTrip.getSource().getName());
                destinationTV.setText(responseTrip.getDestination().getName());

                LatLng sourceLocation = new LatLng(responseTrip.getSource().getLatitutde(), responseTrip.getSource().getLongtiude());
                map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_source))
                        .position(sourceLocation).title(responseTrip.getSource().getName()));

                LatLng destinationLocation = new LatLng(responseTrip.getDestination().getLatitutde(), responseTrip.getDestination().getLongtiude());
                map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_destination))
                        .position(destinationLocation).title(responseTrip.getDestination().getName()));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(destinationLocation, 16));

                // Draw Route Between Source and Destination
                
            }

            @Override
            public void onFailure(Call<Trip> call, Throwable t) {
                Log.e(TAG, "onFailure: Something was wrong: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Failure", Toast.LENGTH_SHORT).show();

                // Check internet connection

                // Check 4XX backend response
            }
        });
    }
}