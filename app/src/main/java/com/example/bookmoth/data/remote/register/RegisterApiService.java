package com.example.bookmoth.data.remote.register;

import com.example.bookmoth.data.model.register.GetOtpRequest;
import com.example.bookmoth.data.model.register.RegisterRequest;
import com.example.bookmoth.data.model.register.VerifyOtpRequest;
import com.example.bookmoth.domain.model.login.Token;
import com.example.bookmoth.domain.model.register.Otp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RegisterApiService {
    @GET("api/account/{email}/exists")
    Call<Void> checkEmailExists(@Path("email") String email);

    @POST("api/otp")
    Call<Otp> getOtp(@Body GetOtpRequest getOtpRequest);

    @POST("api/otp/verify")
    Call<Void> verifyOtp(@Body VerifyOtpRequest request);

    @POST("api/account/register")
    Call<Token> register(@Body RegisterRequest request);
}
