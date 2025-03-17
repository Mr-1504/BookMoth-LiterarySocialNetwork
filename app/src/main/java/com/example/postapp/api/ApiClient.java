package com.example.postapp.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit databaseRetrofit = null;
    private static Retrofit storageRetrofit = null;

    public static SupabaseApi getDatabaseApi() {
        if (databaseRetrofit == null) {
            databaseRetrofit = new Retrofit.Builder()
                    .baseUrl(SupabaseApi.DATABASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return databaseRetrofit.create(SupabaseApi.class);
    }



    public static SupabaseApi getStorageApi() {
        if (storageRetrofit == null) {
            storageRetrofit = new Retrofit.Builder()
                    .baseUrl(SupabaseApi.STORAGE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return storageRetrofit.create(SupabaseApi.class);
    }
}
