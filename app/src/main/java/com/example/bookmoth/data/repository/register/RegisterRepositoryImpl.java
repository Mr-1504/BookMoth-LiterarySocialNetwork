package com.example.bookmoth.data.repository.register;

import com.example.bookmoth.data.model.register.GetOtpRequest;
import com.example.bookmoth.data.model.register.GoogleRegisterRequest;
import com.example.bookmoth.data.model.register.RegisterRequest;
import com.example.bookmoth.data.model.register.TokenResponse;
import com.example.bookmoth.data.model.register.VerifyOtpRequest;
import com.example.bookmoth.data.remote.utils.RetrofitClient;
import com.example.bookmoth.data.remote.register.RegisterApiService;
import com.example.bookmoth.domain.model.register.Otp;
import com.example.bookmoth.domain.repository.register.RegisterRepository;

import retrofit2.Call;

/**
 * Class triển khai {@code RegisterRepository} để xử lý các yêu cầu liên quan đến đăng ký tài khoản.
 */
public class RegisterRepositoryImpl implements RegisterRepository {
    private final RegisterApiService registerApiService;

    /**
     * Khởi tạo repository và thiết lập {@code RegisterApiService} sử dụng Retrofit.
     */
    public RegisterRepositoryImpl() {
        this.registerApiService = RetrofitClient.getAspServerRetrofit().create(RegisterApiService.class);
    }


    /**
     * Gửi yêu cầu lấy mã OTP qua email.
     *
     * @param email Địa chỉ email của người dùng.
     * @param name  Tên của người dùng.
     * @return {@code Call<Otp>} phản hồi chứa mã OTP.
     */
    @Override
    public Call<Otp> getOtp(String email, String name) {
        return registerApiService.getOtp(new GetOtpRequest(email, name));
    }

    /**
     * Kiểm tra xem email đã tồn tại trong hệ thống chưa.
     *
     * @param email Địa chỉ email cần kiểm tra.
     * @return {@code Call<Void>} phản hồi với mã trạng thái HTTP.
     */
    @Override
    public Call<Void> checkEmailExists(String email) {
        return registerApiService.checkEmailExists(email);
    }

    /**
     * Xác minh mã OTP đã nhập.
     *
     * @param email Địa chỉ email của người dùng.
     * @param otp   Mã OTP cần xác minh.
     * @return {@code Call<Void>} phản hồi với mã trạng thái HTTP.
     */
    @Override
    public Call<Void> verifyOtp(String email, String otp) {
        return registerApiService.verifyOtp(new VerifyOtpRequest(email, otp));
    }

    /**
     * Đăng ký tài khoản mới bằng email và mật khẩu.
     *
     * @param firstName   Họ của người dùng.
     * @param lastName    Tên của người dùng.
     * @param email       Địa chỉ email của người dùng.
     * @param password    Mật khẩu tài khoản.
     * @param gender      Giới tính của người dùng (0: Nữ, 1: Nam).
     * @param dateOfBirth Ngày sinh của người dùng (YYYY-MM-DD).
     * @return {@code Call<TokenResponse>} phản hồi chứa token xác thực.
     */
    @Override
    public Call<TokenResponse> register(
            String firstName, String lastName,
            String email, String password,
            int gender, String dateOfBirth
    ) {
        return registerApiService.register(
                new RegisterRequest(firstName, lastName, email, password, gender, dateOfBirth)
        );
    }

    /**
     * Đăng ký tài khoản mới bằng Google.
     *
     * @param idToken Token Google của người dùng.
     * @return {@code Call<TokenResponse>} phản hồi chứa token xác thực.
     */
    @Override
    public Call<TokenResponse> googleRegister(String idToken) {
        return registerApiService.googleRegister(new GoogleRegisterRequest(idToken));
    }
}
