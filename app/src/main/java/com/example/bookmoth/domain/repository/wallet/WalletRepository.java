package com.example.bookmoth.domain.repository.wallet;

import com.example.bookmoth.domain.model.wallet.BalanceResponse;

import retrofit2.Call;

/**
 * Interface chứa các phương thức liên quan đến ví
 */
public interface WalletRepository {
    /**
     * Lấy số dư của ví
     * @return số dư của ví
     */
    Call<BalanceResponse> getBalance();
}
