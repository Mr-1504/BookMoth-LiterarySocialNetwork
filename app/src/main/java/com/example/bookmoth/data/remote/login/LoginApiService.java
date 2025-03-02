package com.example.bookmoth.data.remote.login;

import com.example.bookmoth.data.model.login.LoginRequest;
import com.example.bookmoth.data.model.login.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AuthApi {
    @POST("account/login")
    Call<LoginResponse> login(@Header("apiKey") String apiKey, @Body LoginRequest request);
}
