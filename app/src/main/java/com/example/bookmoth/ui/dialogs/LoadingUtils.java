package com.example.bookmoth.ui.dialogs;

import androidx.fragment.app.FragmentManager;

public class LoadingUtils {
    private static LoadingDialog loadingDialog;

    public static void showLoading(FragmentManager fragmentManager) {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog.newInstance();
            loadingDialog.setCancelable(false);
        }
        if (!loadingDialog.isAdded()) {
            loadingDialog.show(fragmentManager, "LoadingDialog");
        }
    }

    public static void hideLoading() {
        if (loadingDialog != null && loadingDialog.isAdded()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }
}

