package com.example.bookmoth.data.model.payment;

import com.google.gson.annotations.SerializedName;

/**
 * Lớp đại diện cho phản hồi từ ZaloPay khi lấy token thanh toán.
 */
public class ZaloPayTokenResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private String data;

    /**
     * Lấy token ZaloPay.
     *
     * @return Token thanh toán.
     */
    public String getToken() {
        return data;
    }
}
