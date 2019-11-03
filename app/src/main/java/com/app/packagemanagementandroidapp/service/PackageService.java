package com.app.packagemanagementandroidapp.service;

import com.app.packagemanagementandroidapp.model.Pack;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface PackageService {

    @GET("/packages/{id}")
    Call<Pack> getPack(@Path("id") String id, @Header("Authorization") String token);


}
