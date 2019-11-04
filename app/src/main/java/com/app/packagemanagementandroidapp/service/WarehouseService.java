package com.app.packagemanagementandroidapp.service;


import com.app.packagemanagementandroidapp.model.Warehouse;

import retrofit2.Call;

import retrofit2.http.GET;

import retrofit2.http.Path;

public interface WarehouseService {

    @GET("/warehouses/{id}")
    Call<Warehouse> getWarehouse(@Path("id") String id);
}
