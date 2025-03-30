package com.example.bookmoth.core.utils;


import com.example.bookmoth.domain.model.payment.TransactionType;

public class TransactionUtils {
    public static int getTransactionType(TransactionType type) {
        switch (type) {
            case DEPOSIT:
                return 1;
            case WITHDRAWAL:
                return 2;
            default:
                return 1;
        }
    }
}
