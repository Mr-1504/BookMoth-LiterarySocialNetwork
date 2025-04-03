package com.example.bookmoth.data.remote.library;

import com.example.bookmoth.core.libraryutils.AppConst;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit rtf;

    public static Retrofit getInstance() {
        if (rtf == null) {
            rtf = new Retrofit.Builder()
                    .baseUrl(AppConst.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return rtf;
    }
}
