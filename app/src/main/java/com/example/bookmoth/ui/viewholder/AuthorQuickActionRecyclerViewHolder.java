package com.example.bookmoth.ui.viewholder;

import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmoth.R;

public class AuthorQuickActionRecyclerViewHolder extends RecyclerView.ViewHolder {
    public FrameLayout btnNewChapter;
    public FrameLayout btnNewWork;

    public AuthorQuickActionRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        btnNewChapter = itemView.findViewById(R.id.author_fl_newchapter);
        btnNewWork = itemView.findViewById(R.id.author_fl_newwork);
    }
}
