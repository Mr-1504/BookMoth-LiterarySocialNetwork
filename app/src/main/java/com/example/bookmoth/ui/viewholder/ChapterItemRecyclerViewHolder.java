package com.example.bookmoth.ui.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmoth.R;
import com.example.bookmoth.ui.adapter.OnItemClickListener;

public class ChapterItemRecyclerViewHolder extends RecyclerView.ViewHolder {
    public int arrayIndex;
    public TextView index;
    public TextView pdate;
    public TextView title;
    public ImageView indicator_new;

    public ChapterItemRecyclerViewHolder(@NonNull View itemView, OnItemClickListener listener) {
        super(itemView);
        index = itemView.findViewById(R.id.chapter_index);
        pdate = itemView.findViewById(R.id.chapter_pdate);
        title = itemView.findViewById(R.id.chapter_title);
        indicator_new = itemView.findViewById(R.id.indicator_new);

        itemView.setOnClickListener(v -> {
            if (listener != null) {
                int pos = arrayIndex;
                if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onItemClick(pos);
                }
            }
        });
    }
}
