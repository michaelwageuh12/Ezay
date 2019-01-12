package com.example.michael.ezaytask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.example.michael.ezaytask.model.Point;
import com.example.michael.ezaytask.model.Trip;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.net.InetAddress;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String BASE_URL = "http://52.55.117.63:3000/trip/";
    private static final String TAG = "MainActivity";

    // Shared Preference
    public static final String MY_PREFS_NAME = "MyPrefsFile";

    // saveInstanceState keys
    private static final String TRIP_ID = "tripId_key";
    private static final String TRIP_SOURCE = "tripSource_key";
    private static final String TRIP_DESTINATION = "tripDestination_key";
    private static final String TRIP_SOURCE_LAT = "tripSourceLat_key";
    private static final String TRIP_SOURCE_LONG = "tripSourceLong_key";
    private static final String TRIP_DESTINATION_LAT = "tripDestinationLat_key";
    private static final String TRIP_DESTINATION_LONG = "tripDestinationLong_key";

    // Default trip is the first one
    int id = 1;
    public Trip responseTrip;

    ProgressDialog dialog;
    GoogleMap map;
    Button first, prev, next, last;
    SupportMapFragment mapFragment;

    TextView sourceTV, destinationTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialog = new ProgressDialog(MainActivity.this);
        sourceTV = findViewById(R.id.sourceET);
        destinationTV = findViewById(R.id.destinationET);
        first = findViewById(R.id.firstBtn);
        prev = findViewById(R.id.prevtBtn);
        next = findViewById(R.id.nextBtn);
        last = findViewById(R.id.lastBtn);


        if(savedInstanceState != null){
            String tripSource = savedInstanceState.getString(TRIP_SOURCE);
            String tripDestination = savedInstanceState.getString(TRIP_DESTINATION);
            sourceTV.setText(tripSource);
            destinationTV.setText(tripDestination);

            String tripSourceLat = savedInstanceState.getString(TRIP_SOURCE_LAT);
            String tripSourceLon = savedInstanceState.getString(TRIP_SOURCE_LONG);
            String tripDestinationLat = savedInstanceState.getString(TRIP_DESTINATION_LAT);
            String tripDestinationLon = savedInstanceState.getString(TRIP_DESTINATION_LONG);
            id = savedInstanceState.getInt(TRIP_ID);

            Point s = new Point(tripSource,Double.parseDouble(tripSourceLat),Double.parseDouble(tripSourceLon));
            Point d = new Point(tripDestination,Double.parseDouble(tripDestinationLat),Double.parseDouble(tripDestinationLon));

            responseTrip = new Trip(id,s,d);
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setTitle("Loading ...");
                dialog.setMessage("The Next Trip");
                dialog.show();
                if (id < 9)
                    id++;
                getTrip(false);
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setTitle("Loading ...");
                dialog.setMessage("The Previous Trip");
                dialog.show();
                if (id > 1)
                    id--;
                getTrip(false);
            }
        });

        first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setTitle("Loading ...");
                dialog.setMessage("The First Trip");
                dialog.show();
                id = 1;
                getTrip(false);
            }
        });

        last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setTitle("Loading ...");
                dialog.setMessage("The Latest Trip");
                dialog.show();
                id = 9;
                getTrip(true);
            }
        });

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(TRIP_ID, id);

        savedInstanceState.putString(TRIP_SOURCE, sourceTV.getText().toString());
        savedInstanceState.putString(TRIP_SOURCE_LAT, responseTrip.getSource().getLatitutde().toString());
        savedInstanceState.putString(TRIP_SOURCE_LONG, responseTrip.getSource().getLongtiude().toString());

        savedInstanceState.putString(TRIP_DESTINATION, destinationTV.getText().toString());
        savedInstanceState.putString(TRIP_DESTINATION_LAT, responseTrip.getDestination().getLatitutde().toString());
        savedInstanceState.putString(TRIP_DESTINATION_LONG, responseTrip.getDestination().getLongtiude().toString());

        super.onSaveInstanceState(savedInstanceState);
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

        // To show exactly 2 locations of current trip
        map.clear();
        call.enqueue(new Callback<Trip>() {
            @Override
            public void onResponse(Call<Trip> call, Response<Trip> response) {
                dialog.hide();
                Log.d(TAG, "onResponse: Server Response: " + response.toString());
                Log.d(TAG, "onResponse: received information" + response.body().toString());
                responseTrip = response.body();

                sourceTV.setText(responseTrip.getSource().getName());
                destinationTV.setText(responseTrip.getDestination().getName());

                final LatLng sourceLocation = new LatLng(responseTrip.getSource().getLatitutde(), responseTrip.getSource().getLongtiude());
                final LatLng destinationLocation = new LatLng(responseTrip.getDestination().getLatitutde(), responseTrip.getDestination().getLongtiude());

                AddMarker(sourceLocation, destinationLocation);

                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString("source", responseTrip.getSource().getName());
                editor.putString("sourceLat", responseTrip.getSource().getLatitutde().toString());
                editor.putString("sourceLon", responseTrip.getSource().getLongtiude().toString());

                editor.putString("destination", responseTrip.getDestination().getName());
                editor.putString("destinationLat", responseTrip.getDestination().getLatitutde().toString());
                editor.putString("destinationLon", responseTrip.getDestination().getLongtiude().toString());
                editor.apply();

                // Draw Route Between Source and Destination
                DrawRoute(sourceLocation, destinationLocation);
            }

            @Override
            public void onFailure(Call<Trip> call, Throwable t) {
                // Check internet connection
                boolean internetAvailable = CheckInternetConnection();
                boolean isNetworkConnected = isNetworkConnected();
                if (!internetAvailable || !isNetworkConnected) {
                    dialog.setTitle("Connection Lost");
                    dialog.setMessage("Check Internet Connection");
                    dialog.show();
                } else {
                    // Check 4XX backend response
                    dialog.setTitle("Backend Response ERROR");
                    dialog.setMessage("Message: " + t.getMessage());
                    dialog.show();
                }
                SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                String restoredText = prefs.getString("source", "No source defined");
                if (restoredText != null) {
                    String source = prefs.getString("source", "No source defined");
                    Double sourceLat = Double.parseDouble(prefs.getString("sourceLat", null));
                    Double sourceLon = Double.parseDouble(prefs.getString("sourceLon", null));

                    String destination = prefs.getString("destination", "No destination defined");
                    Double destinationLat = Double.parseDouble(prefs.getString("destinationLat", null));
                    Double destinationLon = Double.parseDouble(prefs.getString("destinationLon", null));

                    sourceTV.setText(source);
                    destinationTV.setText(destination);

                    LatLng s1 = new LatLng(sourceLat,sourceLon);
                    LatLng d1 = new LatLng(destinationLat,destinationLon);
                    AddMarker(s1,d1);
                }
            }
        });
    }

    void DrawRoute(final LatLng sourceLocation, final LatLng destinationLocation) {
        GoogleDirection.withServerKey(getString(R.string.google_maps_key))
                .from(sourceLocation)
                .to(destinationLocation)
                .avoid(AvoidType.FERRIES)
                .avoid(AvoidType.HIGHWAYS)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if (direction.isOK()) {
                            Toast.makeText(MainActivity.this, "Loading Route ..", Toast.LENGTH_SHORT).show();
                            Route route = direction.getRouteList().get(0);

                            ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                            PolylineOptions polyline = DirectionConverter.createPolyline(getApplicationContext(), directionPositionList, 5, Color.RED);

                            map.addPolyline(polyline);

                            LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
                            LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
                            LatLngBounds bounds = new LatLngBounds(southwest, northeast);
                            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));

                        } else {
                            // There is a problem when request the route more than 2 times ..
                            /*
                               {"error_message":"You have exceeded your daily request quota for this API. If you did not set a custom daily request quota,
                               verify your project has an active billing account: http://g.co/dev/maps-no-account","routes":[],"status":"OVER_QUERY_LIMIT"}
                             */
                            // So every time i create an API Key then Test my app ...
                            Toast.makeText(MainActivity.this, "You have exceeded your daily request to Directions API", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        Log.d("error", t.getMessage());
                    }
                });
    }

    public void AddMarker(LatLng sourceLocation, LatLng destinationLocation){

        map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_source))
                .position(sourceLocation));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sourceLocation, 12));

        map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_destination))
                .position(destinationLocation));
    }

    public boolean CheckInternetConnection() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            return !ipAddr.equals("");
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


}