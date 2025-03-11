package com.example.bookmoth.data.remote.utils;

import com.example.bookmoth.core.utils.SecureStorage;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class AuthInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = SecureStorage.getToken("jwt_token");
        if (token == null) return chain.proceed(chain.request());

        Request request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer " + token)
                .build();
        return chain.proceed(request);
    }
}

