package com.example.bookmoth.data.remote.login;

import com.example.bookmoth.data.model.login.LoginRequest;
import com.example.bookmoth.domain.model.login.Account;
import com.example.bookmoth.domain.model.login.Token;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface LoginApiService {
//    @Headers({
//            "Content-Type: application/json",
//            "x-api-key: minh"
//    })
    @POST("api/account/login")
    Call<Token> login(@Body LoginRequest request);
}
