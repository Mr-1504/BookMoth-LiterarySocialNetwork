package com.example.bookmoth.ui.viewmodel;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.example.bookmoth.R;
import com.example.bookmoth.core.utils.SecureStorage;
import com.example.bookmoth.data.model.register.TokenResponse;
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

    public void login(Context context, String email, String password, final OnLoginListener listener) {
        loginUseCase.login(email, password).enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Token token = response.body().getData();
                    SecureStorage.saveToken("jwt_token", token.getJwtToken());
                    SecureStorage.saveToken("refresh_token", token.getRefreshToken());
                    listener.onSuccess();
                } else if (response.code() == 401) {
                    listener.onError(context.getString(R.string.invalid_password));
                } else if (response.code() == 404) {
                    listener.onError(context.getString(R.string.account_does_not_exist));
                } else if (response.code() == 500) {
                    listener.onError(context.getString(R.string.error_connecting_to_server));
                } else if (response.code() == 400) {
                    listener.onError(context.getString(R.string.invalid_email));
                } else {
                    listener.onError(context.getString(R.string.undefined_error));
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                listener.onError(context.getString(R.string.error_connecting_to_server));
            }
        });
    }

    public void loginWithGoogle(Context context, String idToken, final OnLoginListener listener) {
        loginUseCase.googleLogin(idToken).enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Token token = response.body().getData();
                    SecureStorage.saveToken("jwt_token", token.getJwtToken());
                    SecureStorage.saveToken("refresh_token", token.getRefreshToken());
                    listener.onSuccess();
                } else if (response.code() == 401) {
                    listener.onError(context.getString(R.string.invalid_google_account));
                } else if (response.code() == 404) {
                    listener.onError(context.getString(R.string.account_does_not_exist));
                } else {
                    listener.onError(context.getString(R.string.undefined_error));
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                listener.onError(context.getString(R.string.error_connecting_to_server));
            }
        });
    }

    public interface OnLoginListener {
        void onSuccess();
        void onError(String error);
    }
}