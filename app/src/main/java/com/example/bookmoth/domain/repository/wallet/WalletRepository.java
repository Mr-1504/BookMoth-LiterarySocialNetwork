package com.example.bookmoth.domain.repository.wallet;

import com.example.bookmoth.data.model.payment.ZaloPayTokenResponse;
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

    /**
     * Tạo đơn hàng để nạp tiền qua ZaloPay.
     *
     * @param amount          Số tiền cần thanh toán (đơn vị: VND).
     * @param description     Mô tả giao dịch.
     * @param transactionType Loại giao dịch (1: nạp, 2: rút, 3: chuyển tiền, 4: thanh toán).
     * @return Đối tượng Call chứa phản hồi từ ZaloPay, bao gồm mã token giao dịch.
     */
    Call<ZaloPayTokenResponse> createOrder(long amount, String description, int transactionType);
}
