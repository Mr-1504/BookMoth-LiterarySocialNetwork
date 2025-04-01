package com.example.bookmoth.core.enums;

import static com.example.bookmoth.core.enums.Transaction.TransactionType.DEPOSIT;
import static com.example.bookmoth.core.enums.Transaction.TransactionType.PAYMENT;

/**
 * Enum đại diện cho loại giao dịch.
 */

public class Transaction {
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
        TRANSFER,
        /**
         * Giao dịch thanh toán
         */
        PAYMENT
    }

    public enum TransactionResult{
        /**
         * Giao dịch thành công
         */
        SUCCESS,
        /**
         * Giao dịch thất bại
         */
        FAILED,
        /**
         * Giao dịch bị hủy
         */
        CANCEL,
        /**
         * Giao dịch lỗi
         */
        ERROR
    }

    public static int getTransactionType(TransactionType type) {
        switch (type) {
            case WITHDRAWAL:
                return 2;
            case TRANSFER:
                return 3;
            case PAYMENT:
                return 4;
            default:
                return 1;
        }
    }

    public static TransactionType getTransactionType(int type) {
        switch (type) {
            case 2:
                return TransactionType.WITHDRAWAL;
            case 3:
                return TransactionType.TRANSFER;
            case 4:
                return PAYMENT;
            default:
                return DEPOSIT;
        }
    }

    public static int getTransactionResult(TransactionResult result) {
        switch (result) {
            case FAILED:
                return 0;
            case CANCEL:
                return -1;
            case SUCCESS:
                return 1;
            default:
                return 2;
        }
    }

    public static TransactionResult getTransactionResult(int result) {
        switch (result) {
            case 0:
                return TransactionResult.FAILED;
            case -1:
                return TransactionResult.CANCEL;
            case 1:
                return TransactionResult.SUCCESS;
            default:
                return TransactionResult.ERROR;
        }
    }
}




