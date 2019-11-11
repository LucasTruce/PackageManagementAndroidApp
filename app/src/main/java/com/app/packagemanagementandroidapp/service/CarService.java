package com.app.packagemanagementandroidapp.service;

import com.app.packagemanagementandroidapp.model.Car;

import retrofit2.Call;

import retrofit2.http.Body;
import retrofit2.http.GET;

import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CarService {

    @GET("/cars/{id}")
    Call<Car> getCar(@Path("id") String id);

    @PUT("/cars")
    Call<Car> updateCar(@Body Car car);
}