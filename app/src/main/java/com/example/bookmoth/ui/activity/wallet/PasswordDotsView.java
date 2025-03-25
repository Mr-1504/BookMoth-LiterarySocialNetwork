package com.example.bookmoth.ui.activity.wallet;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.InputFilter;
import android.text.InputType;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

public class PasswordDotsView extends AppCompatEditText {
    private static final int MAX_LENGTH = 6; // 6 ký tự mật khẩu
    private Paint fillPaint, strokePaint;
    private int dotRadius = 25; // Kích thước ô tròn
    private int spacing = 50; // Khoảng cách giữa các ô

    public PasswordDotsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LENGTH)});
        setBackgroundColor(Color.TRANSPARENT);
        setCursorVisible(false);
        setTextColor(Color.TRANSPARENT);

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setColor(Color.BLUE);
        fillPaint.setStyle(Paint.Style.FILL);

        strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setColor(Color.GRAY);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(4);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int centerY = getHeight() / 2;

        // Tính toán lại để căn giữa theo tổng chiều rộng của tất cả chấm
        int totalWidth = MAX_LENGTH * (dotRadius * 2) + (MAX_LENGTH - 1) * spacing;
        int startX = (width - totalWidth) / 2 + dotRadius; // +dotRadius để vẽ từ tâm

        int length = getText() == null ? 0 : getText().length(); // Kiểm tra null tránh lỗi


        for (int i = 0; i < MAX_LENGTH; i++) {
            if (i < length) {
                // Vẽ chấm tròn xanh nếu đã nhập số
                canvas.drawCircle(startX + i * (dotRadius * 2 + spacing), centerY, dotRadius, fillPaint);
            } else {
                // Vẽ vòng tròn rỗng nếu chưa nhập
                canvas.drawCircle(startX + i * (dotRadius * 2 + spacing), centerY, dotRadius, strokePaint);
            }
        }
    }
}
