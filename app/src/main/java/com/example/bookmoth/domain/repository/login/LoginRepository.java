package com.example.bookmoth.domain.repository.login;

import com.example.bookmoth.data.model.register.TokenResponse;
import com.example.bookmoth.domain.model.login.Account;


import retrofit2.Call;

/**
 * Interface định nghĩa các phương thức liên quan đến đăng nhập và xác thực người dùng.
 */
public interface LoginRepository {

    /**
     * Đăng nhập bằng email và mật khẩu.
     *
     * @param email    Địa chỉ email của người dùng.
     * @param password Mật khẩu của người dùng.
     * @return Đối tượng Call chứa phản hồi TokenResponse.
     */
    Call<TokenResponse> login(String email, String password);

    /**
     * Đăng nhập bằng Google với mã ID Token.
     *
     * @param idToken Mã ID Token do Google cung cấp.
     * @return Đối tượng Call chứa phản hồi TokenResponse.
     */
    Call<TokenResponse> googleLogin(String idToken);

    /**
     * Lấy thông tin tài khoản của người dùng đã đăng nhập.
     *
     * @return Đối tượng Call chứa thông tin tài khoản Account.
     */
    Call<Account> getAccount();

    /**
     * Gửi request đăng xuất phiên hiện tại
     * @param deviceId ID của thiết bị hiện tại
     */
    Call<Void> logout(String deviceId);
}