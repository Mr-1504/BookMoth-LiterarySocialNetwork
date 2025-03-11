package com.example.bookmoth.domain.repository.register;

import com.example.bookmoth.domain.model.login.Token;
import com.example.bookmoth.domain.model.register.Otp;

import java.io.IOException;

import retrofit2.Call;

public interface RegisterRepository {
    Otp getOtp(String email, String name) throws IOException;

    Void checkEmailExists(String email);

    Void verifyOtp(String email, String otp);

    Token register(
            String firstName,
            String lastName,
            String email,
            String password,
            int gender,
            int accountType);
}
