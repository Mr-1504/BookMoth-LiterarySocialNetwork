package com.example.bookmoth.data.repository.payment;

import com.example.bookmoth.data.model.payment.CreateOrderRequest;
import com.example.bookmoth.data.model.payment.ZaloPayTokenResponse;
import com.example.bookmoth.data.remote.payment.PaymentApiService;
import com.example.bookmoth.data.remote.utils.RetrofitClient;
import com.example.bookmoth.domain.repository.payment.PaymentRepository;

import retrofit2.Call;

public class PaymentRepositoryImpl implements PaymentRepository {
    private PaymentApiService paymentApiService;

    public PaymentRepositoryImpl() {
        this.paymentApiService = RetrofitClient.getAspServerRetrofit().create(PaymentApiService.class);
    }

    @Override
    public Call<ZaloPayTokenResponse> createOrder(long amount, String description, boolean transactionType) {
        return paymentApiService.createOrder(new CreateOrderRequest(
            amount,
            description,
            transactionType
        ));
    }
}
