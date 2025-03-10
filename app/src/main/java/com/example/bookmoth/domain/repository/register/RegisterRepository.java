package com.example.bookmoth.domain.repository.register;

import com.example.bookmoth.domain.model.login.Token;
import com.example.bookmoth.domain.model.register.Otp;

import retrofit2.Call;

public interface RegisterRepository {
    Call<Otp> getOtp(String email, String name);

    Call<Void> checkEmailExists(String email);

    Call<Void> verifyOtp(String email, String otp);

    Call<Token> register(
            String firstName,
            String lastName,
            String email,
            String password,
            int gender,
            int accountType);
}
