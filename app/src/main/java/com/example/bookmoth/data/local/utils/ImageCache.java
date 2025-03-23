package com.example.bookmoth.data.local.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Lớp chứa các phương thức xử lý ảnh.
 */
public class ImageCache {

    /**
     * Lưu ảnh dưới dạng file ảnh.
     * @param context Context của ứng dụng.
     * @param bitmap Ảnh cần lưu.
     * @param fileName Tên file ảnh.
     * @return Đường dẫn file ảnh đã lưu.
     */
    public static String saveBitmap(Context context, Bitmap bitmap, String fileName) {
        File file = new File(context.getCacheDir(), fileName);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Tải ảnh từ file ảnh.
     * @param context Context của ứng dụng.
     * @param fileName Tên file ảnh.
     * @return Ảnh đã tải.
     */
    public static Bitmap loadBitmap(Context context, String fileName) {
        File file = new File(context.getCacheDir(), fileName);
        if (file.exists()){
            return BitmapFactory.decodeFile(file.getAbsolutePath());
        }
        return null;
    }
}
