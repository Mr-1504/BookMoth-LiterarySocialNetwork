package com.example.bookmoth.domain.usecase.wallet;

import com.example.bookmoth.domain.model.wallet.BalanceResponse;
import com.example.bookmoth.domain.repository.wallet.WalletRepository;

import retrofit2.Call;

/**
 * Lớp chứa các phương thức liên quan đến ví
 */
public class WalletUseCase {
    private WalletRepository walletRepository;

    /**
     * Khởi tạo WalletUseCase
     * @param walletRepository repository chứa các phương thức gọi API liên quan đến ví
     */
    public WalletUseCase(WalletRepository walletRepository){
        this.walletRepository = walletRepository;
    }

    /**
     * Lấy số dư của ví
     * @return số dư của ví
     */
    public Call<BalanceResponse> getBalance(){
        return walletRepository.getBalance();
    }

    /**
     * Tạo ví mới
     * @return kết quả tạo ví
     */
    public Call<Void> createWallet(String pin) {
        return walletRepository.createWallet(pin);
    }

    /**
     * Xác nhận mã pin
     * @return kết quả xác nhận
     */
    public Call<Void> confirmPin(String pin) {
        return walletRepository.confirmPin(pin);
    }

    /**
     * Kiểm tra ví đã tồn tại chưa
     * @return kết quả kiểm tra
     */
    public Call<Void> checkWalletExist() {
        return walletRepository.checkWalletExist();
    }
}
