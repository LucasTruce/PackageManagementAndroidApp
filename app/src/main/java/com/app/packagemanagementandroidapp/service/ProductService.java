package com.app.packagemanagementandroidapp.service;

import com.app.packagemanagementandroidapp.model.Product;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ProductService {

    @GET("/products/{id}")
    Call<Product> getProduct(@Path("id") String id);
}
