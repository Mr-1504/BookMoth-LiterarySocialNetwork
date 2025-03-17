package com.example.bookmoth.data.remote.login;

import com.example.bookmoth.data.model.login.GoogleLoginRequest;
import com.example.bookmoth.data.model.login.LoginRequest;
import com.example.bookmoth.data.model.register.TokenResponse;
import com.example.bookmoth.domain.model.login.Account;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface LoginApiService {
    @POST("api/account/login")
    Call<TokenResponse> login(@Body LoginRequest request);

    @POST("api/account/auth/google-login")
    Call<TokenResponse> googleLogin(@Body GoogleLoginRequest request);

    @POST("api/account/refresh")
    Call<TokenResponse> refreshToken(@Body String refreshToken);

    @GET("api/account/me")
    Call<Account> getAccount();
}
