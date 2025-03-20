package com.example.bookmoth.data.model.payment;

import com.google.gson.annotations.SerializedName;

public class ZaloPayTokenResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private String data;

    public String getToken() {
        return data;
    }
}
