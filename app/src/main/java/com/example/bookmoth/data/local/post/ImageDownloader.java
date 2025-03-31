package com.example.bookmoth.data.local.post;

import android.content.Context;
import android.util.Log;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class ImageDownloader {
    private final Context context;

    public ImageDownloader(Context context) {
        this.context = context;
    }

    public String downloadAndSaveImage(String url, int id) {
        if (url == null || url.isEmpty()) return null;

        try {
            File imageDir = new File(context.getFilesDir(), "images");
            if (!imageDir.exists()) {
                imageDir.mkdirs();
            }

            String extension = ".jpg";
            if (url.toLowerCase().endsWith(".png")) {
                extension = ".png";
            } else if (url.toLowerCase().endsWith(".jpeg")) {
                extension = ".jpeg";
            }

            File file = new File(imageDir, "post_" + id + extension);
            if (file.exists()) {
                return file.getAbsolutePath();
            }

            File downloadedFile = Glide.with(context)
                    .asFile()
                    .load(url)
                    .submit()
                    .get();

            if (downloadedFile.renameTo(file)) {
                return file.getAbsolutePath();
            } else {
                Log.e("ImageDownloader", "Failed to rename file");
                return null;
            }
        } catch (InterruptedException | ExecutionException e) {
            Log.e("ImageDownloader", "Error downloading image: " + e.getMessage(), e);
            return null;
        }
    }
}