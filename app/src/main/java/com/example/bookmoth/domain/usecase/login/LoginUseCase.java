package com.example.bookmoth.domain.usecase.login;

import com.example.bookmoth.domain.model.login.Account;
import com.example.bookmoth.domain.model.login.Token;
import com.example.bookmoth.domain.repository.login.LoginRepository;

import java.io.IOException;

import retrofit2.Call;

public class LoginUseCase {
    private LoginRepository authRepository;

    public LoginUseCase(LoginRepository authRepository){
        this.authRepository = authRepository;
    }

    public Token login (String email, String password) throws IOException {
        return authRepository.login(email, password);
    }
}
