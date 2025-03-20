package com.example.bookmoth.data.model.register;

/**
 * Class đại diện cho yêu cầu đăng ký tài khoản bằng Google.
 */
public class GoogleRegisterRequest {
    private String idToken;

    /**
     * Khởi tạo yêu cầu đăng ký bằng Google.
     *
     * @param idToken Mã ID token của Google.
     */
    public GoogleRegisterRequest(String idToken) {
        this.idToken = idToken;
    }
}
