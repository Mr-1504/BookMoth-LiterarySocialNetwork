package com.example.bookmoth.domain.usecase.login;

import com.example.bookmoth.data.model.register.TokenResponse;
import com.example.bookmoth.domain.model.login.Account;
import com.example.bookmoth.domain.repository.login.LoginRepository;

import retrofit2.Call;

public class LoginUseCase {
    private final LoginRepository authRepository;

    public LoginUseCase(LoginRepository authRepository){
        this.authRepository = authRepository;
    }

    public Call<TokenResponse> login (String email, String password){
        return  authRepository.login(email, password);
    }

    public Call<TokenResponse> googleLogin(String idToken){
        return authRepository.googleLogin(idToken);
    }

    public Call<Account> getAccount(){
        return authRepository.getAccount();
    }
}
