package com.example.bookmoth.data.network.login;

import com.example.bookmoth.data.model.login.LoginRequest;
import com.example.bookmoth.data.model.login.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {
    @POST("account/login")
    Call<LoginResponse> response(@Body LoginRequest request);
}
