package com.example.bookmoth.data.remote.post;

import com.google.gson.annotations.SerializedName;

public class ApiResponse<T> {
    @SerializedName("data")
    private T data;


    public T getData() {
        return data;
    }
}
