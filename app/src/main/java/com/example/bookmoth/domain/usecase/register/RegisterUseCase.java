package com.example.bookmoth.domain.usecase.register;

import com.example.bookmoth.data.model.register.TokenResponse;
import com.example.bookmoth.domain.model.login.Token;
import com.example.bookmoth.domain.model.register.Otp;
import com.example.bookmoth.domain.repository.register.RegisterRepository;


import retrofit2.Call;

public class RegisterUseCase {
    private final RegisterRepository registerRepository;

    public RegisterUseCase(RegisterRepository registerRepository) {
        this.registerRepository = registerRepository;
    }

    public Call<Otp> getOtpExecute(String email, String name) {
        return registerRepository.getOtp(email, name);
    }

    public Call<Void> checkEmailExistsExecute(String email) {
        return registerRepository.checkEmailExists(email);
    }

    public Call<Void> verifyOtpExecute(String email, String otp) {
        return registerRepository.verifyOtp(email, otp);
    }

    public Call<TokenResponse> registerExecute(
            String firstName,
            String lastName,
            String email,
            String password,
            int gender,
            String dateOfBirth) {
        return registerRepository.register(firstName, lastName, email, password, gender, dateOfBirth);
    }

    public Call<TokenResponse> registerGoogleExecute(String idToken) {
        return registerRepository.googleRegister(idToken);
    }
}
