package com.example.bookmoth.data.repository.login;

import com.example.bookmoth.data.model.login.LoginRequest;
import com.example.bookmoth.data.remote.RetrofitClient;
import com.example.bookmoth.data.remote.login.LoginApiService;
import com.example.bookmoth.domain.model.Account;
import com.example.bookmoth.domain.repository.LoginRepository;

import retrofit2.Call;

public class LoginRepositoryImpl implements LoginRepository {
    private LoginApiService loginApiService;

    public LoginRepositoryImpl(){
        this.loginApiService = RetrofitClient.getInstance().create(LoginApiService.class);
    }

    @Override
    public Call<Account> login(String email, String password) {
        return loginApiService.login(new LoginRequest(email, password));
    }
}
