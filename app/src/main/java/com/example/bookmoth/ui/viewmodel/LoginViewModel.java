package com.example.bookmoth.ui.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.bookmoth.domain.model.Account;
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
                        listener.onError("Sai email hoặc mật khẩu");
                    } else if (response.code() == 404){
                        listener.onError("Tài khoản không tồn tại");
                    } else if (response.code() == 500){
                        listener.onError("Lỗi kết nối server");
                    } else {
                        listener.onError("Lỗi không xác định - " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                listener.onError("Lỗi kết nối đến server: " + t.getMessage());
            }
        });
    }

    public interface OnLoginListener {
        void onSuccess(String token);

        void onError(String error);
    }
}