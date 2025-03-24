package com.example.bookmoth.ui.viewmodel.wallet;

import android.content.Context;

import com.example.bookmoth.R;
import com.example.bookmoth.domain.model.wallet.BalanceResponse;
import com.example.bookmoth.domain.usecase.wallet.WalletUseCase;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Lớp chứa các phương thức liên quan đến ví
 */
public class WalletViewModel {
    private WalletUseCase walletUseCase;

    /**
     * Khởi tạo WalletViewModel
     * @param walletUseCase usecase chứa các phương thức liên quan đến ví
     */
    public WalletViewModel(WalletUseCase walletUseCase){
        this.walletUseCase = walletUseCase;
    }

    public void getBalance(Context context, OnWalletListener listener){
       walletUseCase.getBalance().enqueue(new Callback<BalanceResponse>() {
           @Override
           public void onResponse(Call<BalanceResponse> call, Response<BalanceResponse> response) {
               if (response.isSuccessful() && response.body() != null){
                   listener.onGetBalanceSuccess(response.body());
               } else if (response.code() == 401){
                   listener.onGetBalanceFailed(context.getString(R.string.error_invalid_account));
               } else if (response.code() == 404) {
                   listener.onGetBalanceFailed(context.getString(R.string.account_does_not_exist));
               } else {
                     listener.onGetBalanceFailed(context.getString(R.string.undefined_error));
               }
           }

           @Override
           public void onFailure(Call<BalanceResponse> call, Throwable t) {
                listener.onGetBalanceFailed(context.getString(R.string.error_connecting_to_server));
           }
       });
    }

    public interface OnWalletListener{
        void onGetBalanceSuccess(BalanceResponse balanceResponse);
        void onGetBalanceFailed(String error);
    }
}
