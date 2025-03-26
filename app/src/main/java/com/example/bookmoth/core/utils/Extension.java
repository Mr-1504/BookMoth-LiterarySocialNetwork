package com.example.bookmoth.core.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Extension {
    public static String bigDecimalToAmount(BigDecimal decimal) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator('.');

        DecimalFormat formatter = new DecimalFormat("#,###", symbols);
        return formatter.format(decimal);
    }

    public static String fomatCurrency(String amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator('.');

        DecimalFormat formatter = new DecimalFormat("#,###", symbols);
        return formatter.format(BigDecimal.valueOf(Long.parseLong(amount)));
    }

    public static String normalize(String input) {
        return input.replaceAll("\\.", "");
    }
}
