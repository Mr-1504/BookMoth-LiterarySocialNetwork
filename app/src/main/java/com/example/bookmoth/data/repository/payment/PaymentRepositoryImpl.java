package com.example.bookmoth.data.repository.payment;

import com.example.bookmoth.data.model.payment.CreateOrderRequest;
import com.example.bookmoth.data.model.payment.ZaloPayTokenResponse;
import com.example.bookmoth.data.remote.payment.PaymentApiService;
import com.example.bookmoth.data.remote.utils.RetrofitClient;
import com.example.bookmoth.domain.repository.payment.PaymentRepository;

import retrofit2.Call;

/**
 * Class triển khai {@code PaymentRepository} để xử lý các giao dịch thanh toán.
 */
public class PaymentRepositoryImpl implements PaymentRepository {
    private PaymentApiService paymentApiService;

    /**
     * Khởi tạo repository và thiết lập {@code PaymentApiService} sử dụng Retrofit.
     */
    public PaymentRepositoryImpl() {
        this.paymentApiService = RetrofitClient.getAspServerRetrofit().create(PaymentApiService.class);
    }

    /**
     * Gửi yêu cầu tạo đơn hàng trên ZaloPay.
     *
     * @param amount          Số tiền cần thanh toán.
     * @param description     Mô tả giao dịch.
     * @param transactionType Kiểu giao dịch (true: nạp tiền, false: thanh toán).
     * @return {@code Call<ZaloPayTokenResponse>} phản hồi chứa token thanh toán.
     */
    @Override
    public Call<ZaloPayTokenResponse> createOrder(long amount, String description, boolean transactionType) {
        return paymentApiService.createOrder(new CreateOrderRequest(
            amount,
            description,
            transactionType
        ));
    }
}
