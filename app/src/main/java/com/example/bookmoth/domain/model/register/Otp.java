package com.example.bookmoth.domain.model.register;

/**
 * Class đại diện cho mã OTP (One-Time Password).
 * Lưu trữ mã OTP được sử dụng để xác thực người dùng.
 */
public class Otp {
    private String otpCode;

    /**
     * Khởi tạo đối tượng OTP với mã được cung cấp.
     *
     * @param otpCode Mã OTP ban đầu.
     */
    public Otp(String otpCode) {
        this.otpCode = otpCode;
    }

    /**
     * Lấy mã OTP.
     *
     * @return Mã OTP hiện tại.
     */
    public String getOtpCode() {
        return otpCode;
    }

    /**
     * Cập nhật mã OTP.
     *
     * @param otpCode Mã OTP mới.
     */
    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    /**
     * Trả về chuỗi mô tả đối tượng OTP.
     *
     * @return Chuỗi chứa thông tin mã OTP.
     */
    @Override
    public String toString() {
        return "Otp{" +
                "otpCode='" + otpCode + '\'' +
                '}';
    }
}
