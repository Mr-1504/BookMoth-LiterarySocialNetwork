package com.example.bookmoth.data.remote.login;

import com.example.bookmoth.data.model.login.GoogleLoginRequest;
import com.example.bookmoth.data.model.login.LoginRequest;
import com.example.bookmoth.data.model.login.RefreshTokenRequest;
import com.example.bookmoth.data.model.register.TokenResponse;
import com.example.bookmoth.domain.model.login.Account;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Interface định nghĩa các API endpoint liên quan đến đăng nhập.
 */
public interface LoginApiService {

    /**
     * Gửi yêu cầu đăng nhập bằng email và mật khẩu.
     *
     * @param request Đối tượng {@link LoginRequest} chứa thông tin đăng nhập.
     * @return Đối tượng {@link TokenResponse} chứa token truy cập nếu đăng nhập thành công.
     */
    @POST("api/account/login")
    Call<TokenResponse> login(@Body LoginRequest request);

    /**
     * Đăng nhập bằng tài khoản Google.
     *
     * @param request Đối tượng {@link GoogleLoginRequest} chứa idToken của Google.
     * @return Đối tượng {@link TokenResponse} chứa token truy cập nếu đăng nhập thành công.
     */
    @POST("api/account/auth/google-login")
    Call<TokenResponse> googleLogin(@Body GoogleLoginRequest request);

    /**
     * Làm mới token khi token cũ hết hạn.
     *
     * @param refreshToken Đối tượng {@link RefreshTokenRequest} chứa token cũ để làm mới.
     * @return Đối tượng {@link TokenResponse} chứa token mới.
     */
    @POST("api/account/refresh")
    Call<TokenResponse> refreshToken(@Body RefreshTokenRequest refreshToken);

    /**
     * Lấy thông tin tài khoản hiện tại.
     *
     * @return Đối tượng {@link Account} chứa thông tin tài khoản người dùng.
     */
    @GET("api/account/me")
    Call<Account> getAccount();
}
