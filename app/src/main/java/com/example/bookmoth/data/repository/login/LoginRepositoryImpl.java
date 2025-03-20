package com.example.bookmoth.data.repository.login;

import com.example.bookmoth.data.model.login.GoogleLoginRequest;
import com.example.bookmoth.data.model.login.LoginRequest;
import com.example.bookmoth.data.model.register.TokenResponse;
import com.example.bookmoth.data.remote.utils.RetrofitClient;
import com.example.bookmoth.data.remote.login.LoginApiService;
import com.example.bookmoth.domain.model.login.Account;
import com.example.bookmoth.domain.repository.login.LoginRepository;


import retrofit2.Call;

/**
 * Class triển khai {@code LoginRepository} để thực hiện các tác vụ đăng nhập và lấy thông tin tài khoản.
 */
public class LoginRepositoryImpl implements LoginRepository {
    private final LoginApiService loginApiService;

    /**
     * Khởi tạo repository và thiết lập {@code LoginApiService} sử dụng Retrofit.
     */
    public LoginRepositoryImpl() {
        loginApiService = RetrofitClient.getAspServerRetrofit().create(LoginApiService.class);
    }

    /**
     * Gửi yêu cầu đăng nhập với email và mật khẩu.
     *
     * @param email    Địa chỉ email của người dùng.
     * @param password Mật khẩu của người dùng.
     * @return {@code Call<TokenResponse>} chứa token đăng nhập.
     */
    @Override
    public Call<TokenResponse> login(String email, String password){
        return loginApiService.login(new LoginRequest(email, password));
    }

    /**
     * Gửi yêu cầu đăng nhập bằng Google ID Token.
     *
     * @param idToken Google ID Token.
     * @return {@code Call<TokenResponse>} chứa token đăng nhập.
     */
    @Override
    public Call<TokenResponse> googleLogin(String idToken) {
        return loginApiService.googleLogin(new GoogleLoginRequest(idToken));
    }

    /**
     * Gửi yêu cầu lấy thông tin tài khoản của người dùng đã đăng nhập.
     *
     * @return {@code Call<Account>} chứa thông tin tài khoản.
     */
    @Override
    public Call<Account> getAccount() {
        return loginApiService.getAccount();
    }
}
