package com.example.bookmoth.data.local.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Lớp hỗ trợ lưu trữ và tải ảnh từ bộ nhớ thiết bị.
 */
public class ImageCache {

    /**
     * Lưu ảnh vào bộ nhớ cache.
     *
     * @param context  Context của ứng dụng.
     * @param bitmap   Ảnh cần lưu.
     * @param fileName Tên file ảnh.
     * @return Đường dẫn file ảnh đã lưu hoặc null nếu thất bại.
     */
    public static String saveBitmap(Context context, Bitmap bitmap, String fileName) {
        if (context == null || bitmap == null || fileName == null || fileName.isEmpty()) {
            return null;
        }

        File file = new File(context.getFilesDir(), fileName);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Tải ảnh từ bộ nhớ cache.
     *
     * @param context  Context của ứng dụng.
     * @param fileName Tên file ảnh.
     * @return Ảnh đã tải hoặc null nếu không tìm thấy.
     */
    public static Bitmap loadBitmap(Context context, String fileName) {
        if (context == null || fileName == null || fileName.isEmpty()) {
            return null;
        }

        File file = new File(context.getFilesDir(), fileName);
        Bitmap x = BitmapFactory.decodeFile(file.getAbsolutePath());
        return file.exists() ? BitmapFactory.decodeFile(file.getAbsolutePath()) : null;
    }

    /**
     * Kiểm tra xem file ảnh có tồn tại không.
     *
     * @param context  Context của ứng dụng.
     * @param fileName Tên file ảnh.
     * @return true nếu file tồn tại, false nếu không.
     */
    public static boolean isBitmapExists(Context context, String fileName) {
        if (context == null || fileName == null || fileName.isEmpty()) {
            return false;
        }
        File dir = context.getFilesDir();
        for (File f : dir.listFiles()) {
            Log.d("ImageCache", "File trong thư mục: " + f.getName());
        }

        File file = new File(context.getFilesDir(), fileName);

        Bitmap x = BitmapFactory.decodeFile(file.getAbsolutePath());
        return file.exists();
    }

    /**
     * Xóa ảnh trong bộ nhớ cache.
     *
     * @param context  Context của ứng dụng.
     * @param fileName Tên file ảnh.
     * @return true nếu xóa thành công, false nếu thất bại.
     */
    public static boolean deleteBitmap(Context context, String fileName) {
        if (context == null || fileName == null || fileName.isEmpty()) {
            return false;
        }
        File file = new File(context.getFilesDir(), fileName);
        return file.exists() && file.delete();
    }
}
