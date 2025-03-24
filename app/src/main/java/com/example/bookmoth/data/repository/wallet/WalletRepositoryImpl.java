package com.example.bookmoth.data.repository.wallet;

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
}
