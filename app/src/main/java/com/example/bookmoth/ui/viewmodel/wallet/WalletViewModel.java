package com.example.bookmoth.ui.viewmodel.wallet;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.bookmoth.R;
import com.example.bookmoth.core.enums.PaymentMethod;
import com.example.bookmoth.core.enums.Transaction;
import com.example.bookmoth.data.model.payment.ZaloPayTokenResponse;
import com.example.bookmoth.domain.model.wallet.BalanceResponse;
import com.example.bookmoth.domain.model.wallet.OrderWorkResponse;
import com.example.bookmoth.domain.usecase.wallet.WalletUseCase;
import com.example.bookmoth.ui.activity.login.LoginActivity;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Lớp chứa các phương thức liên quan đến ví
 */
public class WalletViewModel {
    private final WalletUseCase walletUseCase;

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
                } else if (response.code() == 422) {
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
                    if (response.errorBody() != null) {
                        try {
                            JSONObject json = new JSONObject(response.errorBody().string());
                            String errorCode = json.optString("error_code", "");

                            if ("INVALID_PIN".equals(errorCode)) {
                                listener.onFailed(context.getString(R.string.incorrect_pin));
                            } else {
                                Intent intent = new Intent(context, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                Toast.makeText(context, context.getString(R.string.please_login_again), Toast.LENGTH_SHORT).show();
                                context.startActivity(intent);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            listener.onFailed(context.getString(R.string.undefined_error));
                        }
                    } else {
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        Toast.makeText(context, context.getString(R.string.please_login_again), Toast.LENGTH_SHORT).show();
                        context.startActivity(intent);
                    }
                } else if (response.code() == 404) {
                    listener.onFailed(context.getString(R.string.incorrect_pin));
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

    public void createOrder(
            Context context,
            long amount, String description,
            Transaction.TransactionType transactionType,
            final OnCreateOrderListener listener) {
        int type = Transaction.getTransactionType(transactionType);
        walletUseCase.createOrder(amount, description, type)
                .enqueue(new Callback<ZaloPayTokenResponse>() {
                    @Override
                    public void onResponse(Call<ZaloPayTokenResponse> call, Response<ZaloPayTokenResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            listener.onCreateOrderSuccess(response.body());
                        } else {
                            listener.onCreateOrderFailure(context.getString(R.string.undefined_error));
                        }
                    }

                    @Override
                    public void onFailure(Call<ZaloPayTokenResponse> call, Throwable t) {
                        listener.onCreateOrderFailure(context.getString(R.string.error_connecting_to_server));
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
     * Tạo đơn hàng mua sản phẩm
     *
     * @param context   context của activity
     * @param workId    ID của sản phẩm
     * @param orderTime thời gian đặt hàng
     * @param mac       địa chỉ MAC của thiết bị
     * @param listener  listener lắng nghe kết quả
     */
    public void orderProduct(Context context, int workId, String orderTime, String mac, OnOrderProductListener listener) {
        walletUseCase.orderProduct(workId, orderTime, mac).enqueue(new Callback<OrderWorkResponse>() {
            @Override
            public void onResponse(Call<OrderWorkResponse> call, Response<OrderWorkResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else if (response.code() == 400) {
                    listener.onFailed(context.getString(R.string.invalid_data));
                } else if (response.code() == 401) {
                    if (response.errorBody() != null) {
                        try {
                            JSONObject json = new JSONObject(response.errorBody().string());
                            String errorCode = json.optString("error_code", "");

                            if ("INVALID_MAC".equals(errorCode)) {
                                listener.onFailed(context.getString(R.string.invalid_transaction));
                            } else if ("INVALID_TOKEN".equals(errorCode)) {
                                Intent intent = new Intent(context, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                Toast.makeText(context, context.getString(R.string.please_login_again), Toast.LENGTH_SHORT).show();
                                context.startActivity(intent);
                            } else {
                                listener.onFailed(context.getString(R.string.undefined_error));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            listener.onFailed(context.getString(R.string.undefined_error));
                        }
                    }
                } else if (response.code() == 409) {
                    listener.onFailed("409");
                } else if (response.code() == 404) {
                    if (response.errorBody() != null) {
                        try {
                            JSONObject json = new JSONObject(response.errorBody().string());
                            String errorCode = json.optString("error_code", "");

                            if ("INVALID_WALLET".equals(errorCode)) {
                                listener.onFailed("INVALID_WALLET");
                            } else {
                                listener.onFailed(context.getString(R.string.undefined_error));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            listener.onFailed(context.getString(R.string.undefined_error));
                        }
                    }
                } else if (response.code() == 422) {
                    listener.onFailed(context.getString(R.string.cannot_process_request));
                } else {
                    listener.onFailed(context.getString(R.string.undefined_error));
                }
            }

            @Override
            public void onFailure(Call<OrderWorkResponse> call, Throwable t) {
                listener.onFailed(context.getString(R.string.error_connecting_to_server));
            }
        });
    }

    /**
     * Cập nhật phương thức thanh toán cho giao dịch
     *
     * @param context         context của activity
     * @param transactionId   ID của giao dịch
     * @param paymentMethodId ID của phương thức thanh toán
     * @param listener        listener lắng nghe kết quả
     */
    public void updatePaymentMethod(
            Context context, String transactionId, PaymentMethod.Payment_Method paymentMethodId, final OnUpdatePaymentMethodListener listener) {


        walletUseCase.updatePaymentMethod(transactionId, PaymentMethod.getPaymentMethod(paymentMethodId))
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            listener.onUpdatePaymentMethodSuccess();
                        } else if (response.code() == 400) {
                            listener.onUpdatePaymentMethodFailure(context.getString(R.string.invalid_data));
                        } else if (response.code() == 401) {
                            if (response.errorBody() != null) {
                                try {
                                    JSONObject json = new JSONObject(response.errorBody().string());
                                    String errorCode = json.optString("error_code", "");

                                    if ("INVALID_TOKEN".equals(errorCode)) {
                                        Intent intent = new Intent(context, LoginActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        Toast.makeText(context, context.getString(R.string.please_login_again), Toast.LENGTH_SHORT).show();
                                        context.startActivity(intent);
                                    } else {
                                        listener.onUpdatePaymentMethodFailure(context.getString(R.string.undefined_error));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    listener.onUpdatePaymentMethodFailure(context.getString(R.string.undefined_error));
                                }
                            }
                        } else if (response.code() == 404) {
                            if (response.errorBody() != null) {
                                try {
                                    JSONObject json = new JSONObject(response.errorBody().string());
                                    String errorCode = json.optString("error_code", "");

                                    if ("INVALID_WALLET".equals(errorCode)) {
                                        listener.onUpdatePaymentMethodFailure("INVALID_WALLET");
                                    } else if ("INVALID_TRANSACTION".equals(errorCode)) {
                                        listener.onUpdatePaymentMethodFailure("INVALID_TRANSACTION");
                                    } else {
                                        listener.onUpdatePaymentMethodFailure(context.getString(R.string.undefined_error));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    listener.onUpdatePaymentMethodFailure(context.getString(R.string.undefined_error));
                                }
                            }
                        } else if (response.code() == 422) {
                            listener.onUpdatePaymentMethodFailure(context.getString(R.string.cannot_process_request));
                        } else {
                            listener.onUpdatePaymentMethodFailure(context.getString(R.string.undefined_error));
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        listener.onUpdatePaymentMethodFailure(context.getString(R.string.error_connecting_to_server));
                    }
                });
    }

    /**
     * Thanh toán giao dịch
     *
     * @param context       context của activity
     * @param transactionId ID của giao dịch
     * @param listener      listener lắng nghe kết quả
     */
    public void paymentWithWallet(Context context, String transactionId, final OnPaymentListener listener) {
        walletUseCase.payment(transactionId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess();
                } else if (response.code() == 400) {
                    listener.onFailed(context.getString(R.string.invalid_data));
                } else if (response.code() == 401) {
                    if (response.errorBody() != null) {
                        try {
                            JSONObject json = new JSONObject(response.errorBody().string());
                            String errorCode = json.optString("error_code", "");

                            if ("INVALID_TOKEN".equals(errorCode)) {
                                Intent intent = new Intent(context, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                Toast.makeText(context, context.getString(R.string.please_login_again), Toast.LENGTH_SHORT).show();
                                context.startActivity(intent);
                            } else {
                                listener.onFailed(context.getString(R.string.undefined_error));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            listener.onFailed(context.getString(R.string.undefined_error));
                        }
                    }
                } else if (response.code() == 404) {
                    if (response.errorBody() != null) {
                        try {
                            JSONObject json = new JSONObject(response.errorBody().string());
                            String errorCode = json.optString("error_code", "");

                            if ("INVALID_WALLET".equals(errorCode)) {
                                listener.onFailed("INVALID_WALLET");
                            } else if ("INVALID_TRANSACTION".equals(errorCode)) {
                                listener.onFailed("INVALID_TRANSACTION");
                            } else if ("INVALID_RECEIVER_WALLET".equals(errorCode)) {
                                listener.onFailed("INVALID_RECEIVER_WALLET");
                            } else {
                                listener.onFailed(context.getString(R.string.undefined_error));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            listener.onFailed(context.getString(R.string.undefined_error));
                        }
                    }
                } else if (response.code() == 422) {
                    if (response.errorBody() != null) {
                        try {
                            JSONObject json = new JSONObject(response.errorBody().string());
                            String errorCode = json.optString("error_code", "");

                            if ("INSUFFICIENT_FUNDS".equals(errorCode)) {
                                listener.onFailed("INSUFFICIENT_FUNDS");
                            } else {
                                listener.onFailed(context.getString(R.string.cannot_process_request));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            listener.onFailed(context.getString(R.string.undefined_error));
                        }
                    }
                } else {
                    listener.onFailed(context.getString(R.string.undefined_error));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    public void createZaloPayOrder(Context context, String transactionId, final OnCreateOrderListener listener){
        walletUseCase.createZaloPayOrder(transactionId).enqueue(new Callback<ZaloPayTokenResponse>() {
            @Override
            public void onResponse(Call<ZaloPayTokenResponse> call, Response<ZaloPayTokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onCreateOrderSuccess(response.body());
                } else {
                    listener.onCreateOrderFailure(context.getString(R.string.undefined_error));
                }
            }

            @Override
            public void onFailure(Call<ZaloPayTokenResponse> call, Throwable t) {
                listener.onCreateOrderFailure(context.getString(R.string.error_connecting_to_server));
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

    /**
     * Interface lắng nghe kết quả tạo đơn hàng
     */
    public interface OnCreateOrderListener {
        void onCreateOrderSuccess(ZaloPayTokenResponse response);

        void onCreateOrderFailure(String message);
    }

    /**
     * Interface lắng nghe kết quả tạo đơn hàng mua sản phẩm
     */
    public interface OnOrderProductListener {
        void onSuccess(OrderWorkResponse response);

        void onFailed(String error);
    }

    public interface OnUpdatePaymentMethodListener {
        void onUpdatePaymentMethodSuccess();

        void onUpdatePaymentMethodFailure(String message);
    }

    public interface OnPaymentListener {
        void onSuccess();

        void onFailed(String error);
    }
}
