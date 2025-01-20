package com.example.bookmoth.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class InternetHelper {

    /**
     * Kiểm tra xem thiết bị có kết nối Internet hay không.
     *
     * @param context Context của ứng dụng hoặc Activity.
     * @return true nếu có kết nối mạng, false nếu không có kết nối.
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }
}
