package com.example.bookmoth.data.remote.library;

import com.example.bookmoth.core.libraryutils.LibraryConst;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit rtf;

    public static Retrofit getInstance() {
        if (rtf == null) {
            rtf = new Retrofit.Builder()
                    .baseUrl(LibraryConst.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return rtf;
    }
}
