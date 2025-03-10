package com.example.bookmoth.data.repository.register;

import com.example.bookmoth.data.model.register.GetOtpRequest;
import com.example.bookmoth.data.model.register.VerifyOtpRequest;
import com.example.bookmoth.data.remote.RetrofitClient;
import com.example.bookmoth.data.remote.register.RegisterApiService;
import com.example.bookmoth.domain.model.register.EmailCheckResponse;
import com.example.bookmoth.domain.model.register.Otp;
import com.example.bookmoth.domain.repository.register.RegisterRepository;

import retrofit2.Call;

public class RegisterRepositoryImpl implements RegisterRepository {
    private RegisterApiService registerApiService;

    public RegisterRepositoryImpl() {
        this.registerApiService = RetrofitClient.getInstance().create(RegisterApiService.class);
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


}
