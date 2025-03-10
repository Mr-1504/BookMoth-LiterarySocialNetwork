package com.example.bookmoth.data.model.register;

public class VerifyOtpRequest {
    private String email;
    private String otp;

    public VerifyOtpRequest(String email, String otp){
        this.email = email;
        this.otp = otp;
    }
}
