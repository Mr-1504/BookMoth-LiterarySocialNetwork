package com.example.bookmoth.data.model.payment;

public class CreateOrderRequest {
    private long amount;
    private boolean transactionType;
    public String description;

    public CreateOrderRequest(long amount, String description, boolean transactionType) {
        this.amount = amount;
        this.description = description;
        this.transactionType = transactionType;
    }
}
