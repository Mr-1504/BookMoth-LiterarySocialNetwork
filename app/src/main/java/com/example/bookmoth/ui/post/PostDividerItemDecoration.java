package com.example.bookmoth.ui.post;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class PostDividerItemDecoration extends RecyclerView.ItemDecoration {
    private final int space;
    private final Paint paint;

    public PostDividerItemDecoration(Context context, int space, int color) {
        this.space = space;
        paint = new Paint();
        paint.setColor(ContextCompat.getColor(context, color));
        paint.setStrokeWidth(4);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (position == 0) {
            outRect.bottom = space;
        }
    }

    @Override
    public void onDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (parent.getChildCount() > 0) {
            View firstChild = parent.getChildAt(0); // Lấy bài đăng đầu tiên
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();
            int bottom = firstChild.getBottom() + (space / 2); // Vị trí đặt đường kẻ

            canvas.drawLine(left, bottom, right, bottom, paint);
        }
    }
}
