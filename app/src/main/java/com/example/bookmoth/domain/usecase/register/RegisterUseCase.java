package com.example.bookmoth.domain.usecase.register;

import com.example.bookmoth.core.utils.Result;
import com.example.bookmoth.domain.model.login.Token;
import com.example.bookmoth.domain.model.register.Otp;
import com.example.bookmoth.domain.repository.register.RegisterRepository;

import java.io.IOException;

import retrofit2.Call;

public class RegisterUseCase {
    private final RegisterRepository registerRepository;

    public RegisterUseCase(RegisterRepository registerRepository) {
        this.registerRepository = registerRepository;
    }

    public Result<Otp> getOtpExecute(String email, String name) throws IOException {
        return registerRepository.getOtp(email, name);
    }

    public Result<Void> checkEmailExistsExecute(String email) {
        return registerRepository.checkEmailExists(email);
    }

    public Result<Void> verifyOtpExecute(String email, String otp) {
        return registerRepository.verifyOtp(email, otp);
    }

    public Result<Token> registerExecute(
            String firstName,
            String lastName,
            String email,
            String password,
            int gender,
            int accountType
    ) {
        return registerRepository.register(firstName, lastName, email, password, gender, accountType);
    }
}
