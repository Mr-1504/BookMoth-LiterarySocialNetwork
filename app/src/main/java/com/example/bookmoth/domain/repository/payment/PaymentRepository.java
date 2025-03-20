package com.example.bookmoth.domain.repository.payment;

import com.example.bookmoth.data.model.payment.ZaloPayTokenResponse;

import retrofit2.Call;

public interface PaymentRepository {
    Call<ZaloPayTokenResponse> createOrder(long amount, String description, boolean transactionType);
}
