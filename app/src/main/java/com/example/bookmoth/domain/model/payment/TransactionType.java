package com.example.bookmoth.domain.model.payment;

/**
 * Enum đại diện cho loại giao dịch.
 */
public enum TransactionType {
    /** Giao dịch nạp tiền vào tài khoản */
    DEPOSIT,
    /** Giao dịch rút tiền từ tài khoản */
    WITHDRAWAL
}
