package com.example.bookmoth.ui.dialogs;

import androidx.fragment.app.FragmentManager;

/**
 * Tiện ích hiển thị dialog loading
 */
public class LoadingUtils {
    private static LoadingDialog loadingDialog;

    /**
     * Hiển thị dialog loading
     * @param fragmentManager
     */
    public static void showLoading(FragmentManager fragmentManager) {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog.newInstance();
            loadingDialog.setCancelable(false);
        }
        if (!loadingDialog.isAdded()) {
            loadingDialog.show(fragmentManager, "LoadingDialog");
        }
    }

    /**
     * Ẩn dialog loading
     */
    public static void hideLoading() {
        if (loadingDialog != null && loadingDialog.isAdded()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }
}

