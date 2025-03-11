package com.example.bookmoth.ui.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.bookmoth.R;
import com.example.bookmoth.domain.model.login.Account;
import com.example.bookmoth.domain.model.login.Token;
import com.example.bookmoth.domain.usecase.login.LoginUseCase;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends ViewModel {

    private LoginUseCase loginUseCase;

    public LoginViewModel(LoginUseCase loginUseCase) {
        this.loginUseCase = loginUseCase;
    }

    public void login(String email, String password, final OnLoginListener listener) {
        new Thread(() -> {
            try {
                Token token = loginUseCase.login(email, password);
                listener.onSuccess();
            } catch (Exception e) {
                listener.onError(e.getMessage());
            }

        }).start();
    }

    public interface OnLoginListener {
        void onSuccess();

        void onError(String error);
    }
}