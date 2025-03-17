package com.example.bookmoth.domain.repository.login;

import com.example.bookmoth.data.model.register.TokenResponse;
import com.example.bookmoth.domain.model.login.Account;


import retrofit2.Call;

public interface LoginRepository {
    Call<TokenResponse> login(String email, String password);

    Call<TokenResponse> googleLogin(String idToken);

    Call<Account> getAccount();
}