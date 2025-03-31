package com.example.bookmoth.data.model.wallet;

public class OrderProductRequest {
    private int workId;
    private String orderTime;
    private String mac;

    public OrderProductRequest(String mac, String orderTime, int workId) {
        this.mac = mac;
        this.orderTime = orderTime;
        this.workId = workId;
    }
}
