package com.example.bookmoth.core.utils;


import com.example.bookmoth.domain.model.payment.TransactionType;

public class TransactionUtils {
    public static boolean getTransactionType(TransactionType type) {
        switch (type) {
            case DEPOSIT:
                return true;
            case WITHDRAWAL:
                return false;
            default:
                return true;
        }
    }
}
