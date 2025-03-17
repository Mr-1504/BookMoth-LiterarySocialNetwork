package com.example.bookmoth.data.model.register;

import com.example.bookmoth.domain.model.login.Token;
import com.google.gson.annotations.SerializedName;

public class TokenResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private Token data;

    public Token getData() {
        return data;
    }
}
