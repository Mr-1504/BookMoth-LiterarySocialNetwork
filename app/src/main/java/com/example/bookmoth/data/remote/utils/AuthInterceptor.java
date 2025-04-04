package com.example.bookmoth.data.remote.utils;

import androidx.annotation.NonNull;

import com.example.bookmoth.core.utils.SecureStorage;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

/**
 * Interceptor để tự động thêm token xác thực vào tất cả các request HTTP.
 */
public class AuthInterceptor implements Interceptor {

    /**
     * Chặn và sửa đổi request để thêm token xác thực trước khi gửi đi.
     *
     * @param chain Chuỗi request-response trong OkHttp.
     * @return {@link Response} phản hồi từ server sau khi request được xử lý.
     * @throws IOException Nếu có lỗi xảy ra trong quá trình gửi request.
     */
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        String token = SecureStorage.getToken("jwt_token");
        String profileId = SecureStorage.getToken("profileId");

        if (token == null) return chain.proceed(chain.request());

        Request request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("ProfileId", "ProfileId " + profileId)
                .build();
        return chain.proceed(request);
    }
}