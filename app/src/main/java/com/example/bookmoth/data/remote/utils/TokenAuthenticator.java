package com.example.bookmoth.data.remote.utils;

import com.example.bookmoth.core.utils.SecureStorage;
import com.example.bookmoth.data.remote.login.LoginApiService;
import com.example.bookmoth.domain.model.login.Token;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TokenAuthenticator implements Authenticator {
    private final LoginApiService apiService;

    public TokenAuthenticator(String baseUrl) {
        apiService = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(LoginApiService.class);
    }

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        String refreshToken = SecureStorage.getToken("refresh_token");
        if (refreshToken == null) return null;

        retrofit2.Response<Token> tokenResponse = apiService.refreshToken(refreshToken).execute();

        if (tokenResponse.isSuccessful() && tokenResponse.body() != null) {
            Token token = tokenResponse.body();

            SecureStorage.saveToken("jwt_token", token.getJwtToken());
            SecureStorage.saveToken("refresh_token", token.getRefreshToken());

            return response.request().newBuilder()
                    .header("Authorization", "Bearer " + token.getJwtToken())
                    .build();
        }

        SecureStorage.clearToken();
        return null;
    }
}

