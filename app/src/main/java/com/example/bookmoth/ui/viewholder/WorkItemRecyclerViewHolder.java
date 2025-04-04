package com.example.bookmoth.ui.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmoth.R;
import com.example.bookmoth.ui.adapter.OnItemClickListener;

public class WorkItemRecyclerViewHolder extends RecyclerView.ViewHolder {
    public ImageView cover;
    public TextView title;
    public TextView author;
    public ImageView indicator_new;

    public WorkItemRecyclerViewHolder(@NonNull View itemView, OnItemClickListener listener) {
        super(itemView);
        cover = itemView.findViewById(R.id.awork_img_cover);
        title = itemView.findViewById(R.id.awork_tv_title);
        author = itemView.findViewById(R.id.awork_tv_author);
        indicator_new = itemView.findViewById(R.id.indicator_new);

        itemView.setOnClickListener(v -> {
            if (listener != null) {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onItemClick(pos);
                }
            }
        });
    }
}
