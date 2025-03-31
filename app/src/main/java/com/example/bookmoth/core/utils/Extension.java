package com.example.bookmoth.core.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

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

    public static MultipartBody.Part prepareFilePartFromImageView(ImageView imageView, String partName, Context context) {
        // 1. Lấy bitmap từ ImageView
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        // 2. Convert bitmap thành file tạm trong cache
        File file = new File(context.getCacheDir(), "avatar_" + System.currentTimeMillis() + ".jpg");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // 3. Tạo RequestBody
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);

        // 4. Tạo MultipartBody.Part
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

}
