package com.example.bookmoth.core.libraryutils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeFormat {
    private static final SimpleDateFormat inp = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z", Locale.US);
    private static SimpleDateFormat dateOut = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private static SimpleDateFormat timeOut = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private static SimpleDateFormat fullOut = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault());
    private static SimpleDateFormat sqliteOut = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public static final int DATE_ONLY = 0;
    public static final int DATE_TIME = 1;
    public static final int TIME_ONLY = 2;
    public static final int SQLITE = 3;

    public static String format(String value, int mode) {
        try {
            Date date = inp.parse(value);
            if (date != null)
                switch (mode) {
                    case DATE_ONLY: return dateOut.format(date);
                    case TIME_ONLY: return timeOut.format(date);
                    case SQLITE: return sqliteOut.format(date);
                    default: return fullOut.format(date);
                }
            else
                return "";
        } catch (ParseException e) {
            return "";
        }
    }
}
