package com.example.bookmoth.core.enums;

/**
 * Enum đại diện cho loại giao dịch.
 */
public enum TransactionType {
    /**
     * Giao dịch nạp tiền vào tài khoản
     */
    DEPOSIT,
    /**
     * Giao dịch rút tiền từ tài khoản
     */
    WITHDRAWAL,

    /**
     * Giao dịch chuyển tiền
     */
    TRANFER,

    /**
     * Giao dịch thanh toán
     */
    PAYMENT
}
