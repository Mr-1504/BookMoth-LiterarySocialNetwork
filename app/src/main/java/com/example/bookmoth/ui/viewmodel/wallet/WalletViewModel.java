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
     *
     * @param walletUseCase usecase chứa các phương thức liên quan đến ví
     */
    public WalletViewModel(WalletUseCase walletUseCase) {
        this.walletUseCase = walletUseCase;
    }

    /**
     * Lấy số dư của ví
     *
     * @param context  context của activity
     * @param listener listener lắng nghe kết quả
     */
    public void getBalance(Context context, OnWalletListener listener) {
        walletUseCase.getBalance().enqueue(new Callback<BalanceResponse>() {
            @Override
            public void onResponse(Call<BalanceResponse> call, Response<BalanceResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else if (response.code() == 401) {
                    listener.onFailed(context.getString(R.string.error_invalid_account));
                } else if (response.code() == 404) {
                    listener.onFailed(context.getString(R.string.account_does_not_exist));
                } else {
                    listener.onFailed(context.getString(R.string.undefined_error));
                }
            }

            @Override
            public void onFailure(Call<BalanceResponse> call, Throwable t) {
                listener.onFailed(context.getString(R.string.error_connecting_to_server));
            }
        });
    }

    public void createWallet(Context context, String pin, OnWalletListener listener) {
        walletUseCase.createWallet(pin).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess(null);
                } else if (response.code() == 400) {
                    listener.onFailed(context.getString(R.string.invalid_data));
                } else if (response.code() == 401) {
                    listener.onFailed(context.getString(R.string.error_invalid_account));
                } else if (response.code() == 409) {
                    listener.onFailed(context.getString(R.string.wallet_already_exists));
                } else if (response.code() == 422){
                    listener.onFailed(context.getString(R.string.cannot_process_request));
                } else {
                    listener.onFailed(context.getString(R.string.undefined_error));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                listener.onFailed(context.getString(R.string.error_connecting_to_server));
            }
        });
    }

    public void confirmPin(Context context, String pin, OnWalletListener listener) {
        walletUseCase.confirmPin(pin).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess(null);
                } else if (response.code() == 400) {
                    listener.onFailed(context.getString(R.string.invalid_data));
                } else if (response.code() == 401) {
                    listener.onFailed(context.getString(R.string.error_invalid_account));
                } else if (response.code() == 404) {
                    listener.onFailed(context.getString(R.string.wallet_does_not_exist));
                } else {
                    listener.onFailed(context.getString(R.string.undefined_error));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                listener.onFailed(context.getString(R.string.error_connecting_to_server));
            }
        });
    }

    /**
     * Kiểm tra ví đã tồn tại chưa
     *
     * @param context  context của activity
     * @param listener listener lắng nghe kết quả
     */
    public void checkWalletExist(Context context, OnWalletListener listener) {
        walletUseCase.checkWalletExist().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess(null);
                } else if (response.code() == 400) {
                    listener.onFailed(context.getString(R.string.invalid_data));
                } else if (response.code() == 404) {
                    listener.onFailed(context.getString(R.string.wallet_does_not_exist));
                } else {
                    listener.onFailed(context.getString(R.string.undefined_error));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                listener.onFailed(context.getString(R.string.error_connecting_to_server));
            }
        });
    }

    /**
     * Interface lắng nghe kết quả
     */
    public interface OnWalletListener {
        void onSuccess(BalanceResponse balanceResponse);
        void onFailed(String error);
    }


}
