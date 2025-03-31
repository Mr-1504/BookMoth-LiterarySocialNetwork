package com.example.bookmoth.data.model.payment;

/**
 * Class đại diện cho yêu cầu tạo đơn hàng.
 */
public class CreateOrderRequest {
    private long amount;
    private int transactionType;
    public String description;

    /**
     * Khởi tạo yêu cầu tạo đơn hàng.
     *
     * @param amount          Số tiền của đơn hàng.
     * @param description     Mô tả đơn hàng.
     * @param transactionType Loại giao dịch (true: tín dụng, false: ghi nợ).
     */
    public CreateOrderRequest(long amount, String description, int transactionType) {
        this.amount = amount;
        this.description = description;
        this.transactionType = transactionType;
    }
}
