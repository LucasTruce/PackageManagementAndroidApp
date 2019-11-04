package com.app.packagemanagementandroidapp.service;

import com.app.packagemanagementandroidapp.model.Pack;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;

import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PackageService {

    @GET("/packages/{id}")
    Call<Pack> getPack(@Path("id") String id);

    @PUT("/packages")
    Call<Pack> updatePack(@Body Pack pack);

}
