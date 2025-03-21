package com.example.bookmoth.data.model.register;

import com.example.bookmoth.domain.model.login.Token;
import com.google.gson.annotations.SerializedName;

/**
 * Class đại diện cho phản hồi khi yêu cầu token.
 */
public class TokenResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private Token data;

    /**
     * Lấy dữ liệu token.
     *
     * @return Đối tượng Token.
     */
    public Token getData() {
        return data;
    }
}
