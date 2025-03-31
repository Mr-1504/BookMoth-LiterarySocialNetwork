package com.example.bookmoth.data.remote.utils;

import com.example.bookmoth.core.utils.SecureStorage;
import com.example.bookmoth.data.model.login.RefreshTokenRequest;
import com.example.bookmoth.data.model.register.TokenResponse;
import com.example.bookmoth.data.remote.login.LoginApiService;
import com.example.bookmoth.domain.model.login.Token;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Lớp {@code TokenAuthenticator} giúp xử lý việc làm mới (refresh) token JWT khi hết hạn.
 * Nếu token đã hết hạn, nó sẽ tự động gửi request làm mới token và cập nhật token mới vào header của request.
 */
public class TokenAuthenticator implements Authenticator {
    private final LoginApiService apiService;

    /**
     * Khởi tạo TokenAuthenticator với URL của server để gọi API refresh token.
     *
     * @param baseUrl URL của server ASP.NET.
     */
    public TokenAuthenticator(String baseUrl) {
        apiService = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(LoginApiService.class);
    }

    /**
     * Xử lý khi request bị lỗi 401 (Unauthorized), thử làm mới token và gửi lại request.
     *
     * @param route    Route của request.
     * @param response Response lỗi trả về từ server.
     * @return Request mới với token đã cập nhật hoặc {@code null} nếu không thể làm mới token.
     * @throws IOException nếu có lỗi khi gọi API refresh token.
     */
    @Override
    public Request authenticate(Route route, Response response) throws IOException {

        String errorBody = response.peekBody(Long.MAX_VALUE).string();
        String errorCode = null;

        if (errorBody != null && !errorBody.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(errorBody);
                if (jsonObject.has("error_code")) {
                    errorCode = jsonObject.getString("error_code");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!"INVALID_TOKEN".equals(errorCode)) {
            return null;
        }

        String refreshToken = SecureStorage.getToken("refresh_token");
        if (refreshToken == null) return null;

        retrofit2.Response<TokenResponse> tokenResponse = apiService.refreshToken(
                new RefreshTokenRequest(refreshToken)
        ).execute();

        if (tokenResponse.isSuccessful() && tokenResponse.body() != null) {
            Token token = tokenResponse.body().getData();

            SecureStorage.clearToken();

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

