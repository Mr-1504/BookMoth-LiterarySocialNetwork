package com.example.bookmoth.data.model.register;

public class OtpRequest {
    private String email;
    private String name;
    private String otp;

    public OtpRequest(String email, String otp, String name) {
        this.email = email;
        this.otp = otp;
        this.name = name
    }
}
