package com.app.packagemanagementandroidapp.service;

import com.app.packagemanagementandroidapp.model.Login;
import com.app.packagemanagementandroidapp.model.authResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {

    @POST("/authenticate")
    Call<authResponse> authorization(@Body Login login);
}
