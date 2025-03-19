package com.example.bookmoth.domain.usecase.payment;

import com.example.bookmoth.domain.model.payment.ZaloPayTransToken;
import com.example.bookmoth.domain.repository.payment.PaymentRepository;

import retrofit2.Call;

public class PaymentUseCase {
    private final PaymentRepository paymentRepository;

    public PaymentUseCase(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Call<ZaloPayTransToken> createOrder(long amount, String description, boolean transactionType) {
        return paymentRepository.createOrder(amount, description, transactionType);
    }
}
