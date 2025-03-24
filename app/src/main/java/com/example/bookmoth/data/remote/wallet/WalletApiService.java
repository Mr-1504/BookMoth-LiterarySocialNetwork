package com.example.bookmoth.data.remote.wallet;


import com.example.bookmoth.domain.model.wallet.BalanceResponse;

import retrofit2.Call;
import retrofit2.http.GET;

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
}
