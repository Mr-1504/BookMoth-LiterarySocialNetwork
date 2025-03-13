package com.example.bookmoth.data.repository.register;

import com.example.bookmoth.core.utils.Result;
import com.example.bookmoth.data.model.register.GetOtpRequest;
import com.example.bookmoth.data.remote.utils.RetrofitClient;
import com.example.bookmoth.data.remote.register.RegisterApiService;
import com.example.bookmoth.domain.model.login.Token;
import com.example.bookmoth.domain.model.register.Otp;
import com.example.bookmoth.domain.repository.register.RegisterRepository;

import java.io.IOException;

import retrofit2.Response;

public class RegisterRepositoryImpl implements RegisterRepository {
    private final RegisterApiService registerApiService;

    public RegisterRepositoryImpl() {
        this.registerApiService = RetrofitClient.getInstance().create(RegisterApiService.class);
    }

    @Override
    public Result<Otp> getOtp(String email, String name) throws IOException {
        Response<Otp> response = registerApiService.getOtp(new GetOtpRequest(email, name)).execute();
        if (response.isSuccessful() && response.body() != null) {
            return new Result.Success<>(response.body());
        } else {
            throw new IOException("Error connecting to server");
        }
    }

    @Override
    public Result<Void> checkEmailExists(String email) {
        return new Result.Success<>(null);
    }

    @Override
    public Result<Void> verifyOtp(String email, String otp) {
        return null;
    }

    @Override
    public Result<Token> register(
            String firstName,
            String lastName,
            String email,
            String password,
            int gender,
            int accountType) {
        return new Result.Success<>(null);
    }


}
