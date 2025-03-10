package com.example.bookmoth.ui.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.bookmoth.R;
import com.example.bookmoth.domain.model.login.Account;
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
        loginUseCase.execute(email, password).enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body().getEmail());
                } else {
                    if (response.code() == 400){
                        listener.onError(String.valueOf(R.string.incorrect_email_or_password));
                    } else if (response.code() == 404){
                        listener.onError(String.valueOf(R.string.account_does_not_exist));
                    } else if (response.code() == 500){
                        listener.onError(String.valueOf(R.string.error_connecting_to_server));
                    } else {
                        listener.onError(String.valueOf(R.string.undefined_error + response.code()));
                    }
                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                listener.onError(R.string.error_connecting_to_server + t.getMessage());
            }
        });
    }

    public interface OnLoginListener {
        void onSuccess(String token);

        void onError(String error);
    }
}