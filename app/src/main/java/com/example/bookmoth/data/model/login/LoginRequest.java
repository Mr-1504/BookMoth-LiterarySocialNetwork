package com.example.bookmoth.data.model.login;


/**
 * Class đại diện cho yêu cầu đăng nhập bằng email và mật khẩu.
 */
public class LoginRequest {
    private String email;
    private String password;


    /**
     * Khởi tạo đối tượng yêu cầu đăng nhập.
     *
     * @param email    Địa chỉ email của người dùng.
     * @param password Mật khẩu của người dùng.
     */
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
