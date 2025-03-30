package com.example.bookmoth.data.remote.wallet;


import com.example.bookmoth.data.model.payment.CreateOrderRequest;
import com.example.bookmoth.data.model.payment.ZaloPayTokenResponse;
import com.example.bookmoth.data.model.wallet.ConfirmPinRequest;
import com.example.bookmoth.data.model.wallet.CreateWalletRequest;
import com.example.bookmoth.domain.model.wallet.BalanceResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Interface chứa các phương thức gọi API liên quan đến ví
 */
public interface WalletApiService {

    /**
     * Lấy số dư của ví
     * @return số dư của ví
     */
    @GET("api/wallet/balance")
    Call<BalanceResponse> getBalance();

    /**
     * Tạo ví mới
     * @param request thông tin tạo ví
     * @return kết quả tạo ví
     */
    @POST("api/wallet/create")
    Call<Void> createWallet(@Body CreateWalletRequest request);

    /**
     * Xác nhận mã pin
     * @param request mã pin cần xác nhận
     * @return kết quả xác nhận
     */
    @POST("api/wallet/confirm-pin")
    Call<Void> confirmPin(@Body ConfirmPinRequest request);

    /**
     * Kiểm tra ví đã tồn tại chưa
     * @return kết quả kiểm tra
     */
    @GET("api/wallet/exist")
    Call<Void> checkWalletExist();


    /**
     * Tạo đơn hàng để nạp tiền qua ZaloPay.
     *
     * @param createOrderRequest Đối tượng {@link CreateOrderRequest} chứa thông tin giao dịch.
     * @return {@link Call} chứa {@link ZaloPayTokenResponse}, phản hồi từ ZaloPay.
     */
    @POST("api/wallet/deposit")
    Call<ZaloPayTokenResponse> createOrder(@Body CreateOrderRequest createOrderRequest);
}
