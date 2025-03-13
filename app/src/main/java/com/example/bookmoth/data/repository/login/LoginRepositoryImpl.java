package com.example.bookmoth.data.repository.login;

import com.example.bookmoth.data.model.login.GoogleLoginRequest;
import com.example.bookmoth.data.model.login.LoginRequest;
import com.example.bookmoth.data.remote.utils.RetrofitClient;
import com.example.bookmoth.data.remote.login.LoginApiService;
import com.example.bookmoth.domain.model.login.Account;
import com.example.bookmoth.domain.model.login.Token;
import com.example.bookmoth.domain.repository.login.LoginRepository;


import retrofit2.Call;

public class LoginRepositoryImpl implements LoginRepository {
    private final LoginApiService loginApiService;

    public LoginRepositoryImpl() {
        loginApiService = RetrofitClient.getInstance().create(LoginApiService.class);
    }

    @Override
    public Call<Token> login(String email, String password){
        return loginApiService.login(new LoginRequest(email, password));
    }

    @Override
    public Call<Token> googleLogin(String idToken) {
        return loginApiService.googleLogin(new GoogleLoginRequest(idToken));
    }

    @Override
    public Call<Account> getAccount() {
        return loginApiService.getAccount();
    }
}
