package com.example.bookmoth.domain.usecase.login;

import com.example.bookmoth.data.model.register.TokenResponse;
import com.example.bookmoth.domain.model.login.Account;
import com.example.bookmoth.domain.repository.login.LoginRepository;

import retrofit2.Call;

/**
 * Lớp chứa các use case liên quan đến đăng nhập.
 * Đóng vai trò trung gian giữa Repository và ViewModel/Controller.
 */
public class LoginUseCase {
    private final LoginRepository authRepository;

    /**
     * Constructor khởi tạo với repository.
     *
     * @param authRepository Repository xử lý logic xác thực.
     */
    public LoginUseCase(LoginRepository authRepository) {
        this.authRepository = authRepository;
    }

    /**
     * Đăng nhập bằng email và mật khẩu.
     *
     * @param email    Địa chỉ email của người dùng.
     * @param password Mật khẩu của người dùng.
     * @return Đối tượng Call chứa TokenResponse nếu đăng nhập thành công.
     */
    public Call<TokenResponse> login(String email, String password) {
        return authRepository.login(email, password);
    }

    /**
     * Đăng nhập bằng Google OAuth.
     *
     * @param idToken Token xác thực do Google cung cấp.
     * @return Đối tượng Call chứa TokenResponse nếu đăng nhập thành công.
     */
    public Call<TokenResponse> googleLogin(String idToken) {
        return authRepository.googleLogin(idToken);
    }

    /**
     * Lấy thông tin tài khoản của người dùng đã đăng nhập.
     *
     * @return Đối tượng Call chứa thông tin tài khoản {@link Account}.
     */
    public Call<Account> getAccount() {
        return authRepository.getAccount();
    }


    /**
     * Gửi yêu cầu đăng xuất
     * @param deviceId ID của thiết bị
     */
    public Call<Void> logout(String deviceId) {
        return authRepository.logout(deviceId);
    }
}
