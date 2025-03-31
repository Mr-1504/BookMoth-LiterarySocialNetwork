package com.example.bookmoth.core.utils;


import com.example.bookmoth.core.enums.TransactionType;

public class TransactionUtils {
    public static int getTransactionType(TransactionType type) {
        switch (type) {
            case DEPOSIT:
                return 1;
            case WITHDRAWAL:
                return 2;
            case TRANFER:
                return 3;
            case PAYMENT:
                return 4;
            default:
                return 1;
        }
    }
}
