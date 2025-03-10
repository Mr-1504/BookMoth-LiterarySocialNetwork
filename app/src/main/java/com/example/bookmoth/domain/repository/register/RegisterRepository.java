package com.example.bookmoth.domain.repository.register;

import com.example.bookmoth.domain.model.register.EmailCheckResponse;
import com.example.bookmoth.domain.model.register.Otp;

import retrofit2.Call;

public interface RegisterRepository {
    Call<Otp> getOtp(String email, String name);
    Call<Void> checkEmailExists(String email);
    Call<Void> verifyOtp(String email, String otp);
}
