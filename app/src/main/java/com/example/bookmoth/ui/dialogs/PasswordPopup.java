package com.example.bookmoth.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bookmoth.R;
import com.example.bookmoth.ui.activity.wallet.PasswordDotsView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PasswordPopup extends BottomSheetDialogFragment {
    private PasswordDotsView passwordDotsView;
    private StringBuilder passwordBuilder = new StringBuilder();
    private PasswordListener listener;
    private String title;

    public interface PasswordListener {
        void onPasswordEntered(String password);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_password, container, false);

        init(view);

        passwordDotsView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordDotsView.invalidate();
                if (s.length() == 6) {
                    listener.onPasswordEntered(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }

    private void init(View view) {
        passwordDotsView = view.findViewById(R.id.passwordDotsView);
        TextView titleView = view.findViewById(R.id.txtTitle);
        titleView.setText(title);

        int[] btnNumber = new int[]{
                R.id.btn_num_0,
                R.id.btn_num_1,
                R.id.btn_num_2,
                R.id.btn_num_3,
                R.id.btn_num_4,
                R.id.btn_num_5,
                R.id.btn_num_6,
                R.id.btn_num_7,
                R.id.btn_num_8,
                R.id.btn_num_9,
                R.id.btn_backspace
        };


        for (int i = 0; i <= 9; i++) {
            Button button = view.findViewById(btnNumber[i]);
            button.setOnClickListener(v -> addDigit(button.getText().toString(), view));
        }

        Button backspace = view.findViewById(btnNumber[10]);
        backspace.setOnClickListener(v -> removeDigit());
    }

    public PasswordPopup(PasswordListener listener, String title) {
        this.title = title;
        this.listener = listener;
    }

    private void addDigit(String digit, View view) {
        if (passwordBuilder.length() < 6) {
            passwordBuilder.append(digit);
            passwordDotsView.setText(passwordBuilder.toString());
        }
    }

    private void removeDigit() {
        if (passwordBuilder.length() > 0) {
            passwordBuilder.deleteCharAt(passwordBuilder.length() - 1);
            passwordDotsView.setText(passwordBuilder.toString());
        }
    }

    public void setErrorMessage(String message) {
        if (getView() != null) {
            passwordBuilder = new StringBuilder();
            passwordDotsView.setText("");
            TextView error = getView().findViewById(R.id.txtError);
            error.setText(message);
            error.setVisibility(View.VISIBLE);
        }
    }

    public void clear(){
        passwordBuilder = new StringBuilder();
        passwordDotsView.setText("");
        TextView error = getView().findViewById(R.id.txtError);
        error.setText("");
        dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
