package com.example.bookmoth.ui.viewmodel.payment;

import android.content.Context;

import com.example.bookmoth.R;
import com.example.bookmoth.core.utils.TransactionUtils;
import com.example.bookmoth.data.model.payment.ZaloPayTokenResponse;
import com.example.bookmoth.data.repository.payment.PaymentRepositoryImpl;
import com.example.bookmoth.domain.model.payment.TransactionType;
import com.example.bookmoth.domain.model.payment.ZaloPayTransToken;
import com.example.bookmoth.domain.usecase.payment.PaymentUseCase;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentViewModel {
    private PaymentUseCase paymentUseCase;

    public PaymentViewModel() {
        this.paymentUseCase = new PaymentUseCase(new PaymentRepositoryImpl());
    }

    public void createOrder(
            Context context,
            long amount, String description,
            TransactionType transactionType,
            final OnCreateOrderListener listener) {
        boolean type = TransactionUtils.getTransactionType(transactionType);
        paymentUseCase.createOrder(amount, description, type)
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

    public interface OnCreateOrderListener {
        void onCreateOrderSuccess(ZaloPayTokenResponse token);
        void onCreateOrderFailure(String message);
    }
}
