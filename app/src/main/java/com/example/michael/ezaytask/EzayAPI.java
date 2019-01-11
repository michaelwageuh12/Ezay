package com.example.michael.ezaytask;

import com.example.michael.ezaytask.model.Trip;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface EzayAPI {

    String BASE_URL = "http://52.55.117.63:3000/trip/";

    @Headers("Content-Type: application/json")
    @GET("./1")
    Call<Trip> GetFirstTrip();

    @Headers("Content-Type: application/json")
    @GET("./latest")
    Call<Trip> GetLatestTrip();

    @Headers("Content-Type: application/json")
    @GET("./{id}")
    Call<Trip> GetTrip(@Path("id") String id);
}
