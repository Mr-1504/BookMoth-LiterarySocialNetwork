package com.example.bookmoth.domain.repository.register;

import com.example.bookmoth.data.model.register.TokenResponse;
import com.example.bookmoth.domain.model.register.Otp;

import retrofit2.Call;

/**
 * Interface định nghĩa các phương thức liên quan đến đăng ký tài khoản.
 */
public interface RegisterRepository {

    /**
     * Gửi yêu cầu lấy mã OTP qua email.
     *
     * @param email Địa chỉ email của người dùng.
     * @param name  Tên người dùng.
     * @return Đối tượng Call chứa mã OTP.
     */
    Call<Otp> getOtp(String email, String name);

    /**
     * Kiểm tra xem email đã tồn tại trong hệ thống hay chưa.
     *
     * @param email Địa chỉ email cần kiểm tra.
     * @return Đối tượng Call<Void> cho biết kết quả kiểm tra.
     */
    Call<Void> checkEmailExists(String email);

    /**
     * Xác thực mã OTP.
     *
     * @param email Địa chỉ email của người dùng.
     * @param otp   Mã OTP người dùng nhập vào.
     * @return Đối tượng Call<Void> cho biết kết quả xác thực.
     */
    Call<Void> verifyOtp(String email, String otp);

    /**
     * Đăng ký tài khoản mới.
     *
     * @param firstName   Họ của người dùng.
     * @param lastName    Tên của người dùng.
     * @param email       Địa chỉ email của người dùng.
     * @param password    Mật khẩu của tài khoản.
     * @param gender      Giới tính của người dùng (0: Nam, 1: Nữ, 2: Khác).
     * @param dateOfBirth Ngày sinh của người dùng (yyyy-MM-dd).
     * @return Đối tượng Call chứa TokenResponse nếu đăng ký thành công.
     */
    Call<TokenResponse> register(
            String firstName,
            String lastName,
            String email,
            String password,
            int gender,
            String dateOfBirth);

    /**
     * Đăng ký tài khoản bằng Google.
     *
     * @param idToken Mã token xác thực của Google.
     * @return Đối tượng Call chứa TokenResponse nếu đăng ký thành công.
     */
    Call<TokenResponse> googleRegister(String idToken);
}
