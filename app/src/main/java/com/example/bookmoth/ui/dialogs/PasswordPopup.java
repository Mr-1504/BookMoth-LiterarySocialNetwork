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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * Popup nhập mật khẩu
 */
public class PasswordPopup extends BottomSheetDialogFragment {
    private PasswordDotsView passwordDotsView;
    private StringBuilder passwordBuilder = new StringBuilder();
    private PasswordListener listener;
    private String title;

    /**
     * Interface lắng nghe sự kiện nhập mật khẩu
     */
    public interface PasswordListener {
        void onPasswordEntered(String password);
    }

    /**
     * Khởi tạo dialog
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return dialog
     */
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

    /**
     * Khởi tạo view
     * @param view
     */
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

    /**
     * Khởi tạo dialog
     * @param listener
     * @param title
     */
    public PasswordPopup(PasswordListener listener, String title) {
        this.title = title;
        this.listener = listener;
    }

    /**
     * Thêm số vào mật khẩu
     * @param digit
     * @param view
     */
    private void addDigit(String digit, View view) {
        if (passwordBuilder.length() < 6) {
            passwordBuilder.append(digit);
            passwordDotsView.setText(passwordBuilder.toString());
        }
    }

    /**
     * Xóa số khỏi mật khẩu
     */
    private void removeDigit() {
        if (passwordBuilder.length() > 0) {
            passwordBuilder.deleteCharAt(passwordBuilder.length() - 1);
            passwordDotsView.setText(passwordBuilder.toString());
        }
    }

    /**
     * Hiển thị thông báo lỗi
     * @param message
     */
    public void setErrorMessage(String message) {
        if (getView() != null) {
            passwordBuilder = new StringBuilder();
            passwordDotsView.setText("");
            TextView error = getView().findViewById(R.id.txtError);
            error.setText(message);
            error.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Xóa mật khẩu
     */
    public void clear(){
        passwordBuilder = new StringBuilder();
        passwordDotsView.setText("");
        TextView error = getView().findViewById(R.id.txtError);
        error.setText("");
        dismiss();
    }

    /**
     * Khởi tạo dialog
     */
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
