package com.example.bookmoth.domain.repository;

import com.example.bookmoth.data.model.login.LoginRequest;

import retrofit2.Call;

public interface AuthRepository {
    Call<LoginResponse> login (LoginRequest request);
}
