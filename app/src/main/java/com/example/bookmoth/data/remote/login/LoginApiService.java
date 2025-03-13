package com.example.bookmoth.data.remote.login;

import com.example.bookmoth.data.model.login.GoogleLoginRequest;
import com.example.bookmoth.data.model.login.LoginRequest;
import com.example.bookmoth.domain.model.login.Account;
import com.example.bookmoth.domain.model.login.Token;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface LoginApiService {
//    @Headers({
//            "Content-Type: application/json",
//            "x-api-key: minh"
//    })
    @POST("api/account/login")
    Call<Token> login(@Body LoginRequest request);

    @POST("api/account/auth/google-login")
    Call<Token> googleLogin(@Body GoogleLoginRequest request);

    @POST("api/account/refresh")
    Call<Token> refreshToken(@Body String refreshToken);

    @GET("api/account/me")
    Call<Account> getAccount();
}
