package com.example.bookmoth.ui.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmoth.R;

public class WorkDashboardStatsRecyclerViewHolder extends RecyclerView.ViewHolder {
    public ImageView cover;
    public TextView author;
    public TextView title;
    public TextView viewcount;
    public TextView follows;
    public TextView lastupdate;
    public TextView currentprice;

    public WorkDashboardStatsRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        cover = itemView.findViewById(R.id.workdash_img_cover);
        author = itemView.findViewById(R.id.workdash_tv_author);
        title = itemView.findViewById(R.id.workdash_tv_title);
        viewcount = itemView.findViewById(R.id.workdash_tv_view);
        follows = itemView.findViewById(R.id.workdash_tv_follow);
        lastupdate = itemView.findViewById(R.id.workdash_tv_lastupdate);
        currentprice = itemView.findViewById(R.id.workdash_tv_price);
    }
}
