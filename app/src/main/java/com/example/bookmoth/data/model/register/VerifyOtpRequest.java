package com.example.bookmoth.data.model.register;

/**
 * Lớp đại diện cho yêu cầu xác minh OTP.
 */
public class VerifyOtpRequest {
    private String email;
    private String otp;


    /**
     * Khởi tạo VerifyOtpRequest với email và mã OTP.
     *
     * @param email Địa chỉ email của người dùng.
     * @param otp   Mã OTP nhận được.
     */
    public VerifyOtpRequest(String email, String otp) {
        this.email = email;
        this.otp = otp;
    }
}
