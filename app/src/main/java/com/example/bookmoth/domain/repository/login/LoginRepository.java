package com.example.bookmoth.domain.repository.login;

import com.example.bookmoth.domain.model.login.Account;
import com.example.bookmoth.domain.model.login.Token;


import retrofit2.Call;

public interface LoginRepository {
    Call<Token> login(String email, String password);

    Call<Token> googleLogin(String idToken);

    Call<Account> getAccount();
}
