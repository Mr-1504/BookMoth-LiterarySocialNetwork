package com.example.bookmoth.domain.repository.payment;

import com.example.bookmoth.domain.model.payment.ZaloPayTransToken;

import retrofit2.Call;

public interface PaymentRepository {
    Call<ZaloPayTransToken> createOrder(long amount, String description, boolean transactionType);
}
