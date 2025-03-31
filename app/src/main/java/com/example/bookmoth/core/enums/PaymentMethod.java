package com.example.bookmoth.core.enums;

public class PaymentMethod {
    public enum Payment_Method{
        ZaloPay,
        Wallet
    }

    public static int getPaymentMethod(Payment_Method paymentMethod){
        switch (paymentMethod){
            case ZaloPay:
                return 1;
            case Wallet:
                return 2;
            default:
                return 1;
        }
    }
}
