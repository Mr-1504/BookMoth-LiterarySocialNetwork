package com.example.bookmoth.data.remote.payment;

import com.example.bookmoth.data.model.payment.CreateOrderRequest;
import com.example.bookmoth.domain.model.payment.ZaloPayTransToken;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PaymentApiService {
    @POST("api/transaction/deposit")
    Call<ZaloPayTransToken> createOrder(@Body CreateOrderRequest createOrderRequest);
}
