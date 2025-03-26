package com.example.bookmoth.data.model.payment;

import com.google.gson.annotations.SerializedName;

/**
 * Lớp đại diện cho phản hồi từ ZaloPay khi lấy token thanh toán.
 */
public class ZaloPayTokenResponse {

    @SerializedName("zaloToken")
    private String zaloToken;

    @SerializedName("transId")
    private String transId;

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getZaloToken() {
        return zaloToken;
    }

    public void setZaloToken(String zaloToken) {
        this.zaloToken = zaloToken;
    }
}
