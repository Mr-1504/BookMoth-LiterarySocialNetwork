package com.example.bookmoth.domain.repository.login;

import com.example.bookmoth.domain.model.login.Account;

import retrofit2.Call;

public interface LoginRepository {
    Call<Account> login (String email, String password);
}
