package com.example.bookmoth.domain.repository.payment;

import com.example.bookmoth.data.model.payment.ZaloPayTokenResponse;

import retrofit2.Call;

/**
 * Interface định nghĩa các phương thức liên quan đến thanh toán qua ZaloPay.
 */
public interface PaymentRepository {

    /**
     * Tạo đơn hàng mới trên ZaloPay.
     *
     * @param amount          Số tiền cần thanh toán (đơn vị: VND).
     * @param description     Mô tả giao dịch.
     * @param transactionType Loại giao dịch (true: nạp, false: rút).
     * @return Đối tượng Call chứa phản hồi từ ZaloPay, bao gồm mã token giao dịch.
     */
    Call<ZaloPayTokenResponse> createOrder(long amount, String description, int transactionType);
}