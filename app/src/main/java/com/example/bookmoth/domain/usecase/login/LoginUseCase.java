package com.example.bookmoth.domain.usecase.login;

import com.example.bookmoth.data.model.login.LoginRequest;
import com.example.bookmoth.domain.model.Account;
import com.example.bookmoth.domain.repository.LoginRepository;

import retrofit2.Call;

public class LoginUseCase {
    private LoginRepository authRepository;

    public LoginUseCase(LoginRepository authRepository){
        this.authRepository = authRepository;
    }

    public Call<Account>execute(String email, String password){
        return authRepository.login(email, password);
    }
}
