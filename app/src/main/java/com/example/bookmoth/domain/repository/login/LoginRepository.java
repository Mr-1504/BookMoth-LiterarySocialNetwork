package com.example.bookmoth.domain.repository.login;

import com.example.bookmoth.domain.model.login.Account;
import com.example.bookmoth.domain.model.login.Token;

import java.io.IOException;

import retrofit2.Call;

public interface LoginRepository {
    Token login (String email, String password) throws IOException;
}
