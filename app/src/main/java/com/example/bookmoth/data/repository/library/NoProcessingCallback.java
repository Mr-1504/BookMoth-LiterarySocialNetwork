package com.example.bookmoth.data.repository.library;

import com.example.bookmoth.core.libraryutils.InnerCallback;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class NoProcessingCallback {
    static <T> Callback<T> make(InnerCallback<T> callback) {
        return new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    try {
                        callback.onError(response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                callback.onError(t.toString());
            }
        };
    }
}
