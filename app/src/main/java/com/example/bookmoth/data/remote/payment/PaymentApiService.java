package com.example.bookmoth.data.remote.payment;

import com.example.bookmoth.data.model.payment.CreateOrderRequest;
import com.example.bookmoth.data.model.payment.ZaloPayTokenResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Interface định nghĩa API liên quan đến thanh toán.
 */
public interface PaymentApiService {
    /**
     * Tạo đơn hàng để nạp tiền qua ZaloPay.
     *
     * @param createOrderRequest Đối tượng {@link CreateOrderRequest} chứa thông tin giao dịch.
     * @return {@link Call} chứa {@link ZaloPayTokenResponse}, phản hồi từ ZaloPay.
     */
    @POST("api/transaction/deposit")
    Call<ZaloPayTokenResponse> createOrder(@Body CreateOrderRequest createOrderRequest);
}
