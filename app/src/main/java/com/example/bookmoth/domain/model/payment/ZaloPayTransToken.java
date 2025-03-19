package com.example.bookmoth.domain.model.payment;

public class ZaloPayTransToken {
    private String zptranstoken;

    public ZaloPayTransToken(String zptranstoken) {
        this.zptranstoken = zptranstoken;
    }

    public String getZptranstoken() {
        return zptranstoken;
    }

    public void setZptranstoken(String zptranstoken) {
        this.zptranstoken = zptranstoken;
    }
}
