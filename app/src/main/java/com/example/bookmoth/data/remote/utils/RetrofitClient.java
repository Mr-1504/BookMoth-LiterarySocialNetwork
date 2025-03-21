package com.example.bookmoth.data.remote.utils;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_ASP_SERVER_URL = "http://127.0.0.1:7100/";
    private static final String SUPABASE_URL = "https://vhqcdiaoqrlcsnqvjpqh.supabase.co/";
    private static final String FLASK_URL = "http://127.0.0.1:5000/";


    private static Retrofit aspServerRetrofit;
    private static Retrofit supabaseRetrofit;
    private static Retrofit flaskRetrofit;

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
    public static Retrofit getServerPost() {
        if (supabaseRetrofit == null) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);


            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(httpLoggingInterceptor)
                    .addInterceptor(new SubabaseApiKeyInterceptor())
                    .build();
            supabaseRetrofit = new Retrofit.Builder()
                    .baseUrl(SUPABASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return supabaseRetrofit;
    }
    public static Retrofit getFlaskRetrofit() {
        if (flaskRetrofit == null) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(httpLoggingInterceptor)
                    .build();

            flaskRetrofit = new Retrofit.Builder()
                    .baseUrl(FLASK_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return flaskRetrofit;
    }
}
