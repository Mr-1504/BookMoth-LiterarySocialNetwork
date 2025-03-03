package com.example.bookmoth.data.remote.login;

import com.example.bookmoth.data.model.login.LoginRequest;
import com.example.bookmoth.domain.model.login.Account;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginApiService {
    @POST("account/login")
    Call<Account> login(@Body LoginRequest request);
}
