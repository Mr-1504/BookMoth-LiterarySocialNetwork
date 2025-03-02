package com.example.bookmoth.data.repository.login;

import com.example.bookmoth.data.model.login.LoginRequest;
import com.example.bookmoth.data.model.login.LoginResponse;
import com.example.bookmoth.data.remote.RetrofitClient;
import com.example.bookmoth.data.remote.login.AuthApi;
import com.example.bookmoth.domain.repository.AuthRepository;

import retrofit2.Call;

public class AuthRepositoryImpl implements AuthRepository {
    private AuthApi authApi;

    public AuthRepositoryImpl(AuthApi authApi){
        this.authApi = RetrofitClient.getInstance().create(AuthApi.class);
    }
    @Override
    public Call<LoginResponse> login(LoginRequest request) {
        return authApi.login(request);
    }
}
