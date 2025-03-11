package com.example.bookmoth.data.repository.register;

import com.example.bookmoth.data.model.register.GetOtpRequest;
import com.example.bookmoth.data.model.register.RegisterRequest;
import com.example.bookmoth.data.model.register.VerifyOtpRequest;
import com.example.bookmoth.data.remote.utils.RetrofitClient;
import com.example.bookmoth.data.remote.register.RegisterApiService;
import com.example.bookmoth.domain.model.login.Token;
import com.example.bookmoth.domain.model.register.Otp;
import com.example.bookmoth.domain.repository.register.RegisterRepository;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class RegisterRepositoryImpl implements RegisterRepository {
    private final RegisterApiService registerApiService;

    public RegisterRepositoryImpl() {
        this.registerApiService = RetrofitClient.getInstance().create(RegisterApiService.class);
    }

    @Override
    public Otp getOtp(String email, String name) throws IOException {
        Response<Otp> response = registerApiService.getOtp(new GetOtpRequest(email, name)).execute();
        if (response.isSuccessful() && response.body() != null) {
            return response.body();
        } else {
            throw new IOException("Error connecting to server");
        }
    }

    @Override
    public Call<Void> checkEmailExists(String email) {
        return registerApiService.checkEmailExists(email);
    }

    @Override
    public Call<Void> verifyOtp(String email, String otp) {
        return registerApiService.verifyOtp(new VerifyOtpRequest(email, otp));
    }

    @Override
    public Call<Token> register(
            String firstName,
            String lastName,
            String email,
            String password,
            int gender,
            int accountType) {
        return registerApiService.register(
                new RegisterRequest(firstName, lastName, email, password, gender, accountType));
    }


}
