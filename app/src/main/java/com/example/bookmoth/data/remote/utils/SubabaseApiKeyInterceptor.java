package com.example.bookmoth.data.remote.utils;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.http.Headers;

public class SubabaseApiKeyInterceptor implements Interceptor {
    String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InZocWNkaWFvcXJsY3NucXZqcHFoIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mzg2NTY4NjcsImV4cCI6MjA1NDIzMjg2N30.uZ1zHyL4LMAsy_BcNE8MbJz73jw_9Mhj7dHtVKds6Qw";

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request original = chain.request();

        // Thêm API Key vào Header
        Request request = original.newBuilder()
                .header("apikey", SUPABASE_KEY)
                .header("Authorization", "Bearer "+ SUPABASE_KEY)
                .header("Content-Type","application/json")
                .build();

        return chain.proceed(request);
    }

}
