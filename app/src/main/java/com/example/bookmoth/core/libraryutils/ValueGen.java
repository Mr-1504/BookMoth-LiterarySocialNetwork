package com.example.bookmoth.core.libraryutils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.IOException;

public class ValueGen {
    public static int transparencyHexToSolidPercent(int argbInt) {
        int apart = (argbInt >> 24) & 0xFF;
        return (int)((1 - (apart/255f))*100);
    }
    public static String makeTransparencyParseColorValue(int solidPercent, int color) {
        int alpha = Math.max(0, Math.min(255, (int)(255-(solidPercent/100.0)*255)));

        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;

        return String.format("#%02X%02X%02X%02X", alpha, red, green, blue);
    }

    public static void makeEmptyFile(File file) {
        try {
            file.createNewFile();
        } catch (IOException ignored) {}
    }

    public static String getFileNameFromUri(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    public static <T> T isNull(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }
}
