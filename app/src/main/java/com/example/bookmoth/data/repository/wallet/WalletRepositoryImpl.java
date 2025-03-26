package com.example.bookmoth.data.repository.wallet;

import com.example.bookmoth.data.model.wallet.ConfirmPinRequest;
import com.example.bookmoth.data.model.wallet.CreateWalletRequest;
import com.example.bookmoth.data.remote.utils.RetrofitClient;
import com.example.bookmoth.data.remote.wallet.WalletApiService;
import com.example.bookmoth.domain.model.wallet.BalanceResponse;
import com.example.bookmoth.domain.repository.wallet.WalletRepository;

import retrofit2.Call;

/**
 * Lớp chứa các phương thức gọi API liên quan đến ví
 */
public class WalletRepositoryImpl implements WalletRepository {
   private final WalletApiService walletApiService;

   /**
    * Khởi tạo WalletRepositoryImpl
    */
   public WalletRepositoryImpl(){
         walletApiService = RetrofitClient.getAspServerRetrofit().create(WalletApiService.class);
   }

   /**
    * Lấy số dư của ví
    * @return số dư của ví
    */
   public Call<BalanceResponse> getBalance(){
         return walletApiService.getBalance();
   }

    /**
     * Tạo ví mới
     * @return kết quả tạo ví
     */
    @Override
    public Call<Void> createWallet(String pin) {
        return walletApiService.createWallet(new CreateWalletRequest(pin));
    }

    /**
     * Xác nhận mã pin
     * @return kết quả xác nhận
     */
    @Override
    public Call<Void> confirmPin(String pin) {
        return walletApiService.confirmPin(new ConfirmPinRequest(pin));
    }

    /**
     * Kiểm tra ví đã tồn tại chưa
     * @return kết quả kiểm tra
     */
    @Override
    public Call<Void> checkWalletExist() {
        return walletApiService.checkWalletExist();
    }
}
