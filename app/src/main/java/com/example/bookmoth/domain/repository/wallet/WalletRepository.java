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

    /**
     * Tạo ví mới
     * @return kết quả tạo ví
     */
    Call<Void> createWallet(String pin);

    /**
     * Xác nhận mã pin
     * @return kết quả xác nhận
     */
    Call<Void> confirmPin(String pin);

    /**
     * Kiểm tra ví đã tồn tại chưa
     * @return kết quả kiểm tra
     */
    Call<Void> checkWalletExist();
}
