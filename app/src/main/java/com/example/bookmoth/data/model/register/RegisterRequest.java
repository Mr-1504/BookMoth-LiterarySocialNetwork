package com.example.bookmoth.data.model.register;

/**
 * Class đại diện cho yêu cầu đăng ký tài khoản mới.
 */
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private int gender;
    private String dateOfBirth;

    /**
     * Khởi tạo một yêu cầu đăng ký mới.
     *
     * @param firstName   Họ của người dùng.
     * @param lastName    Tên của người dùng.
     * @param email       Địa chỉ email.
     * @param password    Mật khẩu tài khoản.
     * @param gender      Giới tính (0: Nam, 1: Nữ, 2: Khác).
     * @param dateOfBirth Ngày sinh của người dùng (định dạng YYYY-MM-DD).
     */
    public RegisterRequest(
            String firstName,
            String lastName,
            String email,
            String password,
            int gender,
            String dateOfBirth
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
    }
}
