package com.example.bookmoth.domain.usecase.payment;

import com.example.bookmoth.data.model.payment.ZaloPayTokenResponse;
import com.example.bookmoth.domain.repository.payment.PaymentRepository;

import retrofit2.Call;

/**
 * Lớp chứa các use case liên quan đến thanh toán.
 * Đóng vai trò trung gian giữa Repository và ViewModel/Controller.
 */
public class PaymentUseCase {
    private final PaymentRepository paymentRepository;

    /**
     * Constructor khởi tạo `PaymentUseCase` với `PaymentRepository`.
     *
     * @param paymentRepository Repository xử lý logic thanh toán.
     */
    public PaymentUseCase(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    /**
     * Tạo đơn hàng thanh toán qua ZaloPay.
     *
     * @param amount          Số tiền cần thanh toán.
     * @param description     Mô tả giao dịch.
     * @param transactionType Loại giao dịch (true: giao dịch ngay, false: giao dịch tạm giữ).
     * @return Đối tượng Call chứa phản hồi từ ZaloPay.
     */
    public Call<ZaloPayTokenResponse> createOrder(long amount, String description, boolean transactionType) {
        return paymentRepository.createOrder(amount, description, transactionType);
    }
}
