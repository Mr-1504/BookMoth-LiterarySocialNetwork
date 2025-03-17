package com.example.bookmoth.data.repository.register;

import com.example.bookmoth.data.model.register.GetOtpRequest;
import com.example.bookmoth.data.model.register.GoogleRegisterRequest;
import com.example.bookmoth.data.model.register.RegisterRequest;
import com.example.bookmoth.data.model.register.TokenResponse;
import com.example.bookmoth.data.model.register.VerifyOtpRequest;
import com.example.bookmoth.data.remote.utils.RetrofitClient;
import com.example.bookmoth.data.remote.register.RegisterApiService;
import com.example.bookmoth.domain.model.login.Token;
import com.example.bookmoth.domain.model.register.Otp;
import com.example.bookmoth.domain.repository.register.RegisterRepository;

import retrofit2.Call;


public class RegisterRepositoryImpl implements RegisterRepository {
    private final RegisterApiService registerApiService;

    public RegisterRepositoryImpl() {
        this.registerApiService = RetrofitClient.getAspServerRetrofit().create(RegisterApiService.class);
    }


    @Override
    public Call<Otp> getOtp(String email, String name) {
        return registerApiService.getOtp(new GetOtpRequest(email, name));
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
    public Call<TokenResponse> register(
            String firstName, String lastName,
            String email, String password,
            int gender, String dateOfBirth
    ) {
        return registerApiService.register(
                new RegisterRequest(firstName, lastName, email, password, gender, dateOfBirth)
        );
    }

    @Override
    public Call<TokenResponse> googleRegister(String idToken) {
        return registerApiService.googleRegister(new GoogleRegisterRequest(idToken));
    }
}
