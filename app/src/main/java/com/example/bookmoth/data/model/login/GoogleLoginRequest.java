package com.example.bookmoth.data.model.login;

/**
 * Class yêu cầu đăng nhập bằng Google.
 */
public class GoogleLoginRequest {
    private String idToken;

    /**
     * Khởi tạo đối tượng yêu cầu đăng nhập Google.
     *
     * @param idToken Mã token của Google ID, dùng để xác thực người dùng.
     */
    public GoogleLoginRequest(String idToken) {
        this.idToken = idToken;
    }
}