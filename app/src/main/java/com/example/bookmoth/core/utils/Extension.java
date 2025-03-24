package com.example.bookmoth.core.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Extension {
    public static String BigDecimalToAmount(BigDecimal decimal) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');

        DecimalFormat formatter = new DecimalFormat("#,### VND", symbols);
        return formatter.format(decimal);
    }
}
