package com.example.bookmoth.core.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Lớp tiện ích chứa các phương thức mở rộng cho việc xử lý dữ liệu.
 */
public class Extension {


    public static String bigDecimalToAmount(BigDecimal decimal) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator('.');

        DecimalFormat formatter = new DecimalFormat("#,###", symbols);
        return formatter.format(decimal);
    }

    /**
     * Chuẩn hóa số tiền thành chuỗi có định dạng.
     *
     * @param amount Số tiền cần chuẩn hóa.
     * @return Chuỗi đã chuẩn hóa.
     */
    public static String fomatCurrency(String amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator('.');

        DecimalFormat formatter = new DecimalFormat("#,###", symbols);
        return formatter.format(BigDecimal.valueOf(Long.parseLong(amount)));
    }

    /**
     * Chuẩn hóa chuỗi bỏ dấu.
     *
     * @param input Chuỗi cần chuẩn hóa.
     * @return Chuỗi đã chuẩn hóa.
     */
    public static String normalize(String input) {
        return input.replaceAll("\\.", "");
    }

    /**
     * Chuẩn bị MultipartBody.Part từ ImageView.
     *
     * @param imageView ImageView chứa ảnh cần tải lên.
     * @param partName   Tên của phần dữ liệu.
     * @param context    Context của ứng dụng.
     * @return MultipartBody.Part đã chuẩn bị.
     */
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

    /**
     * Lấy timestamp hiện tại.
     *
     * @return Timestamp hiện tại.
     */
    public static long getTimeStamp() {
        return System.currentTimeMillis();
    }

}
