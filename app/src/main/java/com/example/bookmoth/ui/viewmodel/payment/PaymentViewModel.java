package com.example.bookmoth.ui.viewmodel.payment;

import android.content.Context;

import com.example.bookmoth.R;
import com.example.bookmoth.data.repository.payment.PaymentRepositoryImpl;
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
            boolean transactionType,
            final OnCreateOrderListener listener) {
        paymentUseCase.createOrder(amount, description, transactionType)
                .enqueue(new Callback<ZaloPayTransToken>() {
                    @Override
                    public void onResponse(Call<ZaloPayTransToken> call, Response<ZaloPayTransToken> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            listener.onCreateOrderSuccess(response.body().getZptranstoken());
                        } else {
                            listener.onCreateOrderFailure(context.getString(R.string.undefined_error));
                        }
                    }

                    @Override
                    public void onFailure(Call<ZaloPayTransToken> call, Throwable t) {
                        listener.onCreateOrderFailure(context.getString(R.string.error_connecting_to_server));
                    }
                });
    }

    public interface OnCreateOrderListener {
        void onCreateOrderSuccess(String zaloPayTransToken);
        void onCreateOrderFailure(String message);
    }
}
