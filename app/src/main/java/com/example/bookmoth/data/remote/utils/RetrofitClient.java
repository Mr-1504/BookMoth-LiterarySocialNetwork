package com.example.bookmoth.data.remote.utils;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_ASP_SERVER_URL = "http://127.0.0.1:7100/";
    private static Retrofit aspServerRetrofit;

    public static Retrofit getAspServerRetrofit() {
        if (aspServerRetrofit == null) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);


            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(httpLoggingInterceptor)
                    .addInterceptor(new AuthInterceptor())
                    .authenticator(new TokenAuthenticator(BASE_ASP_SERVER_URL))
                    .build();
            aspServerRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_ASP_SERVER_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return aspServerRetrofit;
    }
}
