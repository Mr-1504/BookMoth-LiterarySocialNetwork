package com.example.bookmoth.data.repository.login;

import com.example.bookmoth.core.utils.SecureStorage;
import com.example.bookmoth.data.model.login.LoginRequest;
import com.example.bookmoth.data.remote.utils.RetrofitClient;
import com.example.bookmoth.data.remote.login.LoginApiService;
import com.example.bookmoth.domain.model.login.Account;
import com.example.bookmoth.domain.model.login.Token;
import com.example.bookmoth.domain.repository.login.LoginRepository;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class LoginRepositoryImpl implements LoginRepository {
    private LoginApiService loginApiService;

    public LoginRepositoryImpl(){
        this.loginApiService = RetrofitClient.getInstance().create(LoginApiService.class);
    }

    @Override
    public Token login(String email, String password) throws IOException {
        Response<Token> response = loginApiService.login(new LoginRequest(email, password)).execute();
        if (response.isSuccessful() && response.body() != null) {
            Token token = response.body();
            SecureStorage.saveToken("access_token", token.getJwtToken());
            SecureStorage.saveToken("refresh_token", token.getRefreshToken());
            return token;
        } else {
            throw new IOException("Error connecting to server");
        }
    }
}
