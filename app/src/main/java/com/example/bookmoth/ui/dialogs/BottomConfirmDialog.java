package com.example.bookmoth.ui.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.example.bookmoth.R;

public class BottomConfirmDialog extends BottomSheetDialog {
    private View dialogView;
    private TextView tvTitle;
    private TextView tvClarifyOne;
    private FrameLayout clarifyFrame;
    private TextView tvActionNote;
    private Button btnCancel;
    private Button btnSubmit;
    private String ori_submittext;
    private int clickNeeded = 0;
    private int clickClicked = 0;

    public BottomConfirmDialog(@NonNull Context context) {
        super(context);
        dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_general_confirms, null);
        super.setContentView(dialogView);

        btnCancel = dialogView.findViewById(R.id.confirm_cancel);
        btnSubmit = dialogView.findViewById(R.id.confirm_submit);
        ori_submittext = btnSubmit.getText().toString();
    }

    public void setTitle(String title) {
        tvTitle = dialogView.findViewById(R.id.dgcf_title);
        tvTitle.setText(title);
        tvTitle.setVisibility(View.VISIBLE);
    }

    public void setClarifyText(String clarify) {
        tvClarifyOne = dialogView.findViewById(R.id.dgcf_clarify1);
        tvClarifyOne.setText(clarify);
        tvClarifyOne.setVisibility(clarify == null || clarify.isBlank() ? View.GONE : View.VISIBLE);
    }

    public void setClarifyView(View v) {
        clarifyFrame = dialogView.findViewById(R.id.dgcf_fl_additionview);
        clarifyFrame.removeAllViews();
        clarifyFrame.addView(v);
        clarifyFrame.setVisibility(v == null ? View.GONE : View.VISIBLE);
    }

    public void setFinalActionNote(String note) {
        tvActionNote = dialogView.findViewById(R.id.dgcf_clarify2);
        tvActionNote.setText(note);
        tvActionNote.setVisibility(note == null || note.isBlank() ? View.GONE : View.VISIBLE);
    }

    public void setSubmitClickNeeded(int count) {
        clickNeeded = count > 0 ? count : 1;
        btnSubmit.setText(String.format("%s (%d)", ori_submittext, clickNeeded-clickClicked));
    }

    public void setOnMadeDecisionListener(OnMadeDecisionListener listener) {
        btnCancel.setOnClickListener(v -> listener.cancel());
        btnSubmit.setOnClickListener(v -> {
            clickClicked++;
            if (clickNeeded > clickClicked)
                btnSubmit.setText(String.format("%s (%d)", ori_submittext, clickNeeded-clickClicked));
            else if (clickNeeded == clickClicked)
                btnSubmit.setText(ori_submittext);
            else
                listener.submit();
        });
    }

    public interface OnMadeDecisionListener {
        void cancel();
        void submit();
    }

    @Override
    public void setContentView(View view) {} //skip this to make custom view
}
