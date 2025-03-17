package com.example.bookmoth.ui.viewmodel.registerViewModel;


import android.content.Context;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.example.bookmoth.R;
import com.example.bookmoth.core.utils.DatetimeHelper;
import com.example.bookmoth.core.utils.GenderUtils;
import com.example.bookmoth.core.utils.SecureStorage;
import com.example.bookmoth.data.model.register.TokenResponse;
import com.example.bookmoth.domain.model.Gender;
import com.example.bookmoth.domain.model.login.Token;
import com.example.bookmoth.domain.model.register.Otp;
import com.example.bookmoth.domain.usecase.register.RegisterUseCase;

import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterViewModel extends ViewModel implements Serializable {
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private Gender gender;
    private String email;
    private String password;
    private String otp;

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void checkEmailExists(Context context, RegisterUseCase useCase, final OnCheckEmailExistsListener listener) {
        useCase.checkEmailExistsExecute(this.getEmail()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                int statusCode = response.code();

                if (statusCode == 200) {
                    Log.d("EmailCheck", "Email đã tồn tại");
                    listener.onError(context.getString(R.string.email_already_exists));
                } else if (statusCode == 204) {
                    Log.d("EmailCheck", "Email chưa tồn tại");
                    listener.onSuccess();
                } else {
                    Log.d("EmailCheck", "Mã trạng thái không mong đợi: " + statusCode);
                    listener.onError(context.getString(R.string.undefined_error));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                listener.onError(context.getString(R.string.error_connecting_to_server));
            }
        });
    }

    public void getOtp(Context context, RegisterUseCase registerUseCase, final OnGetOtpListener listener) {
        registerUseCase.getOtpExecute(
                this.getEmail(),
                this.getFirstName()
        ).enqueue(new Callback<Otp>() {
            @Override
            public void onResponse(Call<Otp> call, Response<Otp> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess();
                } else if (response.code() == 409) {
                    listener.onError(context.getString(R.string.email_already_exists));
                } else if (response.code() == 400) {
                    listener.onError(context.getString(R.string.invalid_email)); // Sửa lỗi ở đây
                } else if (response.code() == 422) {
                    listener.onError(context.getString(R.string.cannot_send_otp)); // Sửa lỗi ở đây
                } else {
                    listener.onError(context.getString(R.string.undefined_error) + " " + response.code()); // Sửa lỗi ở đây
                }
            }

            @Override
            public void onFailure(Call<Otp> call, Throwable t) {
                listener.onError(context.getString(R.string.error_connecting_to_server) + " " + t.getMessage()); // Sửa lỗi ở đây
            }
        });
    }

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
                    listener.onSuccess();
                } else if (response.code() == 400) {
                    listener.onError(context.getString(R.string.invalid_email));
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

    public interface OnGetOtpListener {
        void onSuccess();

        void onError(String error);
    }

    public interface OnCheckEmailExistsListener {
        void onSuccess();

        void onError(String error);
    }

    public interface OnVerifyOtpListener {
        void onSuccess();

        void onError(String error);
    }

    public interface OnRegisterListener {
        void onSuccess();

        void onError(String error);
    }
}

