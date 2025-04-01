package com.example.bookmoth.data.model.wallet;

public class UpdatePaymentMethodRequest {
    private String transactionId;
    private int paymentMethodId;

    public UpdatePaymentMethodRequest(String transactionId, int paymentMethodId) {
        this.transactionId = transactionId;
        this.paymentMethodId = paymentMethodId;
    }
}
