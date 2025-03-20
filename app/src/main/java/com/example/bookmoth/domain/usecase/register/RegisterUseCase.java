package com.example.bookmoth.domain.usecase.register;

import com.example.bookmoth.data.model.register.TokenResponse;
import com.example.bookmoth.domain.model.register.Otp;
import com.example.bookmoth.domain.repository.register.RegisterRepository;
import retrofit2.Call;

/**
 * Lớp chứa các use case liên quan đến quá trình đăng ký tài khoản.
 * Đóng vai trò trung gian giữa Repository và ViewModel/Controller.
 */
public class RegisterUseCase {
    private final RegisterRepository registerRepository;

    /**
     * Constructor khởi tạo `RegisterUseCase` với `RegisterRepository`.
     *
     * @param registerRepository Repository xử lý logic đăng ký tài khoản.
     */
    public RegisterUseCase(RegisterRepository registerRepository) {
        this.registerRepository = registerRepository;
    }

    /**
     * Gửi yêu cầu lấy mã OTP qua email.
     *
     * @param email Địa chỉ email của người dùng.
     * @param name  Tên của người dùng.
     * @return Đối tượng Call chứa thông tin OTP.
     */
    public Call<Otp> getOtpExecute(String email, String name) {
        return registerRepository.getOtp(email, name);
    }

    /**
     * Kiểm tra xem email đã tồn tại trong hệ thống chưa.
     *
     * @param email Địa chỉ email cần kiểm tra.
     * @return Đối tượng Call trả về null nếu email chưa tồn tại, lỗi nếu đã tồn tại.
     */
    public Call<Void> checkEmailExistsExecute(String email) {
        return registerRepository.checkEmailExists(email);
    }

    /**
     * Xác minh mã OTP do người dùng nhập.
     *
     * @param email Địa chỉ email của người dùng.
     * @param otp   Mã OTP được gửi đến email.
     * @return Đối tượng Call chứa kết quả xác minh (thành công hoặc thất bại).
     */
    public Call<Void> verifyOtpExecute(String email, String otp) {
        return registerRepository.verifyOtp(email, otp);
    }

    /**
     * Đăng ký tài khoản mới với thông tin do người dùng cung cấp.
     *
     * @param firstName   Họ của người dùng.
     * @param lastName    Tên của người dùng.
     * @param email       Địa chỉ email.
     * @param password    Mật khẩu tài khoản.
     * @param gender      Giới tính (0: nữ, 1: nam).
     * @param dateOfBirth Ngày sinh của người dùng (yyyy-MM-dd).
     * @return Đối tượng Call chứa `TokenResponse` nếu đăng ký thành công.
     */
    public Call<TokenResponse> registerExecute(
            String firstName,
            String lastName,
            String email,
            String password,
            int gender,
            String dateOfBirth) {
        return registerRepository.register(firstName, lastName, email, password, gender, dateOfBirth);
    }

    /**
     * Đăng ký tài khoản bằng tài khoản Google.
     *
     * @param idToken Mã token từ Google Sign-In.
     * @return Đối tượng Call chứa `TokenResponse` nếu đăng ký thành công.
     */
    public Call<TokenResponse> registerGoogleExecute(String idToken) {
        return registerRepository.googleRegister(idToken);
    }
}
