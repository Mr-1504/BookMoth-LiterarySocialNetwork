package com.example.bookmoth.data.model.register;

/**
 * Class đại diện cho yêu cầu lấy mã OTP (One-Time Password) qua email.
 */
public class GetOtpRequest {
    private String email;
    private String name;

    /**
     * Khởi tạo yêu cầu lấy OTP với email và tên người dùng.
     *
     * @param email Địa chỉ email của người dùng.
     * @param name  Tên của người dùng.
     */
    public GetOtpRequest(String email, String name) {
        this.email = email;
        this.name = name;
    }
}
