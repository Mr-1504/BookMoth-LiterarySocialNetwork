package com.example.bookmoth.ui.viewmodel.register;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.example.bookmoth.R;
import com.example.bookmoth.core.services.MyFirebaseMessagingService;
import com.example.bookmoth.core.utils.GenderUtils;
import com.example.bookmoth.core.utils.SecureStorage;
import com.example.bookmoth.data.model.register.TokenResponse;
import com.example.bookmoth.domain.model.profile.Gender;
import com.example.bookmoth.domain.model.login.Token;
import com.example.bookmoth.domain.model.register.Otp;
import com.example.bookmoth.domain.usecase.register.RegisterUseCase;

import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel cho quá trình đăng ký người dùng.
 * Quản lý dữ liệu đăng ký, kiểm tra email, OTP và thực hiện đăng ký.
 */
public class RegisterViewModel extends ViewModel implements Serializable {
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private Gender gender;
    private String email;
    private String password;
    private String otp;

    /**
     * Lấy tên đầu của người dùng.
     *
     * @return firstName của người dùng.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Thiết lập tên đầu của người dùng.
     *
     * @param firstName Tên đầu cần thiết lập.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Lấy họ của người dùng.
     *
     * @return lastName của người dùng.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Thiết lập họ của người dùng.
     *
     * @param lastName Họ cần thiết lập.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Lấy email của người dùng.
     *
     * @return Email của người dùng.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Thiết lập email của người dùng.
     *
     * @param email Email cần thiết lập.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Lấy mật khẩu của người dùng.
     *
     * @return Password của người dùng.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Thiết lập mật khẩu của người dùng.
     *
     * @param password Mật khẩu cần thiết lập.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Lấy giới tính của người dùng.
     *
     * @return Gender của người dùng.
     */
    public Gender getGender() {
        return gender;
    }

    /**
     * Thiết lập giới tính của người dùng.
     *
     * @param gender Giới tính cần thiết lập.
     */
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    /**
     * Lấy mã OTP hiện tại.
     *
     * @return OTP hiện tại.
     */
    public String getOtp() {
        return otp;
    }

    /**
     * Thiết lập mã OTP.
     *
     * @param otp Mã OTP.
     */
    public void setOtp(String otp) {
        this.otp = otp;
    }

    /**
     * Lấy ngày sinh của người dùng
     *
     * @return ngày sinh
     */
    public String getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Thiết lập ngày sinh của người dùng
     *
     * @param dateOfBirth ngày sinh
     */
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * Kiểm tra xem email có tồn tại hay không.
     *
     * @param context  Context của ứng dụng.
     * @param useCase  Đối tượng RegisterUseCase để thực hiện kiểm tra.
     * @param listener Lắng nghe kết quả kiểm tra.
     */
    public void checkEmailExists(Context context, RegisterUseCase useCase, final OnCheckEmailExistsListener listener) {
        useCase.checkEmailExistsExecute(this.getEmail()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    listener.onError(context.getString(R.string.email_already_exists));
                } else if (statusCode == 204) {
                    listener.onSuccess();
                } else {
                    listener.onError(context.getString(R.string.undefined_error));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                listener.onError(context.getString(R.string.error_connecting_to_server));
            }
        });
    }

    /**
     * Yêu cầu gửi OTP đến email.
     *
     * @param context         Context của ứng dụng.
     * @param registerUseCase Đối tượng xử lý yêu cầu OTP.
     * @param listener        Lắng nghe kết quả OTP.
     */
    public void getOtp(Context context, RegisterUseCase registerUseCase, final OnGetOtpListener listener) {
        registerUseCase.getOtpExecute(this.getEmail(), this.getFirstName()).enqueue(new Callback<Otp>() {
            @Override
            public void onResponse(Call<Otp> call, Response<Otp> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess();
                } else {
                    listener.onError(context.getString(R.string.undefined_error));
                }
            }

            @Override
            public void onFailure(Call<Otp> call, Throwable t) {
                listener.onError(context.getString(R.string.error_connecting_to_server));
            }
        });
    }

    /**
     * Xác minh OTP.
     *
     * @param context         Context của ứng dụng.
     * @param registerUseCase Đối tượng xử lý xác minh OTP.
     * @param listener        Lắng nghe kết quả xác minh OTP.
     */
    public void verifyOtp(Context context, RegisterUseCase registerUseCase, final OnVerifyOtpListener listener) {
        registerUseCase.verifyOtpExecute(this.getEmail(), this.getOtp()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess();
                } else {
                    listener.onError(context.getString(R.string.invalid_otp));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                listener.onError(context.getString(R.string.error_connecting_to_server));
            }
        });
    }

    /**
     * Đăng ký người dùng.
     *
     * @param context         Context của ứng dụng.
     * @param registerUseCase Đối tượng xử lý đăng ký.
     * @param listener        Lắng nghe kết quả đăng ký.
     */
    public void register(Context context, RegisterUseCase registerUseCase, final OnRegisterListener listener) {
        registerUseCase.registerExecute(
                this.getFirstName(),
                this.getLastName(),
                this.getEmail(),
                this.getPassword(),
                GenderUtils.getGenderIntValue(this.getGender()),
                this.getDateOfBirth()
        ).enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Token token = response.body().getData();
                    SecureStorage.saveToken("jwt_token", token.getJwtToken());
                    SecureStorage.saveToken("refresh_token", token.getRefreshToken());

                    new MyFirebaseMessagingService().updateTokenToServer(context);

                    listener.onSuccess();
                } else {
                    listener.onError(context.getString(R.string.undefined_error));
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                listener.onError(context.getString(R.string.error_connecting_to_server));
            }
        });
    }

    /**
     *
     * @param context
     * @param registerUseCase
     * @param idToken
     * @param listener
     */
    public void registerWithGoogle(
            Context context,
            RegisterUseCase registerUseCase,
            String idToken,
            final OnRegisterListener listener
    ) {
        registerUseCase.registerGoogleExecute(idToken).enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    Token token = response.body().getData();
                    SecureStorage.saveToken("jwt_token", token.getJwtToken());
                    SecureStorage.saveToken("refresh_token", token.getRefreshToken());

                    new MyFirebaseMessagingService().updateTokenToServer(context);

                    listener.onSuccess();
                } else if (response.code() == 400) {
                    listener.onError(context.getString(R.string.invalid_email));
                } else if (response.code() == 401) {
                    listener.onError(context.getString(R.string.invalid_google_account));
                } else if (response.code() == 409) {
                    listener.onError(context.getString(R.string.email_already_exists));
                } else if (response.code() == 422) {
                    listener.onError(context.getString(R.string.cannot_register));
                } else {
                    listener.onError(context.getString(R.string.undefined_error) + " " + response.code());
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                listener.onError(context.getString(R.string.error_connecting_to_server));
            }
        });
    }

    /**
     * Interface lắng nghe sự kiện lấy OTP.
     */
    public interface OnGetOtpListener {
        void onSuccess();

        void onError(String error);
    }

    /**
     * Interface lắng nghe kiểm tra email tồn tại.
     */
    public interface OnCheckEmailExistsListener {
        void onSuccess();

        void onError(String error);
    }

    /**
     * Interface lắng nghe xác minh OTP.
     */
    public interface OnVerifyOtpListener {
        void onSuccess();

        void onError(String error);
    }

    /**
     * Interface lắng nghe đăng ký.
     */
    public interface OnRegisterListener {
        void onSuccess();

        void onError(String error);
    }
}
