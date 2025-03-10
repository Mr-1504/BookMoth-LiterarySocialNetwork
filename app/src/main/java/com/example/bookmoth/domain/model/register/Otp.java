package com.example.bookmoth.domain.model.register;

public class Otp {
    private String otpCode;

    public Otp(String otpCode) {
        this.otpCode = otpCode;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }
}
