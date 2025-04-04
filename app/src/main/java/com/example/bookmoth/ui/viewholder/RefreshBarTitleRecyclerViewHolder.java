package com.example.bookmoth.ui.viewholder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmoth.R;

public class RefreshBarTitleRecyclerViewHolder extends RecyclerView.ViewHolder {
    public TextView title;
    public ImageButton btn_refresh;

    public RefreshBarTitleRecyclerViewHolder(@NonNull View itemView, View.OnClickListener refreshListener) {
        super(itemView);
        title = itemView.findViewById(R.id.tv_bartitle);
        btn_refresh = itemView.findViewById(R.id.imgbtn_refresh);
        btn_refresh.setOnClickListener(refreshListener);
    }
}
