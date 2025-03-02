package com.example.bookmoth.domain.repository;

import com.example.bookmoth.domain.model.Account;

import retrofit2.Call;

public interface LoginRepository {
    Call<Account> login (String email, String password);
}
