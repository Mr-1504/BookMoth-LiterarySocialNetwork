package com.example.bookmoth.data.remote.payment;

import com.example.bookmoth.data.model.payment.CreateOrderRequest;
import com.example.bookmoth.data.model.payment.ZaloPayTokenResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PaymentApiService {
    @POST("api/transaction/deposit")
    Call<ZaloPayTokenResponse> createOrder(@Body CreateOrderRequest createOrderRequest);
}
