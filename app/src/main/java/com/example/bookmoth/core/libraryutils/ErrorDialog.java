package com.example.bookmoth.core.libraryutils;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.example.bookmoth.R;

public class ErrorDialog {
    public static void showError(Context context, String error) {
        Dialog errDiag = new Dialog(context);
        errDiag.setContentView(R.layout.dialog_error);
        ((TextView) errDiag.findViewById(R.id.tv_errormessage)).setText(error);
        errDiag.show();
    }
}
