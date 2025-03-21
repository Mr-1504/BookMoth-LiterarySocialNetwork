package com.example.bookmoth.data.remote.register;

import com.example.bookmoth.data.model.register.GetOtpRequest;
import com.example.bookmoth.data.model.register.GoogleRegisterRequest;
import com.example.bookmoth.data.model.register.RegisterRequest;
import com.example.bookmoth.data.model.register.TokenResponse;
import com.example.bookmoth.data.model.register.VerifyOtpRequest;
import com.example.bookmoth.domain.model.register.Otp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Interface định nghĩa API liên quan đến đăng ký tài khoản.
 */
public interface RegisterApiService {

    /**
     * Kiểm tra xem email đã tồn tại trong hệ thống hay chưa.
     *
     * @param email Địa chỉ email cần kiểm tra.
     * @return {@link Call} chứa phản hồi từ API, nếu email tồn tại sẽ trả về lỗi.
     */
    @GET("api/account/{email}/exists")
    Call<Void> checkEmailExists(@Path("email") String email);

    /**
     * Gửi yêu cầu lấy mã OTP.
     *
     * @param getOtpRequest Đối tượng chứa thông tin email và tên người dùng.
     * @return {@link Call} chứa thông tin mã OTP từ API.
     */
    @POST("api/otp")
    Call<Otp> getOtp(@Body GetOtpRequest getOtpRequest);

    /**
     * Xác minh mã OTP đã nhập.
     *
     * @param request Đối tượng chứa email và mã OTP cần xác minh.
     * @return {@link Call} phản hồi từ API, nếu mã OTP hợp lệ thì trả về thành công.
     */
    @POST("api/otp/verify")
    Call<Void> verifyOtp(@Body VerifyOtpRequest request);

    /**
     * Đăng ký tài khoản mới bằng email và mật khẩu.
     *
     * @param request Đối tượng chứa thông tin đăng ký như họ, tên, email, mật khẩu, giới tính, ngày sinh.
     * @return {@link Call} chứa {@link TokenResponse} phản hồi từ API sau khi đăng ký thành công.
     */
    @POST("api/account/register")
    Call<TokenResponse> register(@Body RegisterRequest request);

    /**
     * Đăng ký tài khoản mới bằng Google.
     *
     * @param request Đối tượng chứa mã ID Token của Google.
     * @return {@link Call} chứa {@link TokenResponse} phản hồi từ API sau khi đăng ký thành công.
     */
    @POST("api/account/auth/google-register")
    Call<TokenResponse> googleRegister(@Body GoogleRegisterRequest request);
}
