package com.example.bookmoth.core.utils;

import java.text.SimpleDateFormat;

public class DatetimeHelper {
    public static String getFormattedDate(String date) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");

            // Chuyển từ Date -> String (yyyy-MM-dd)
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

            return outputFormat.format(date);
        } catch (Exception e) {
            return null;
        }
    }
}
