package com.example.bookmoth.domain.repository.register;

import com.example.bookmoth.core.utils.Result;
import com.example.bookmoth.domain.model.login.Token;
import com.example.bookmoth.domain.model.register.Otp;

import java.io.IOException;


public interface RegisterRepository {
    Result<Otp> getOtp(String email, String name) throws IOException;

    Result<Void> checkEmailExists(String email);

    Result<Void> verifyOtp(String email, String otp);

    Result<Token> register(
            String firstName,
            String lastName,
            String email,
            String password,
            int gender,
            int accountType);
}
