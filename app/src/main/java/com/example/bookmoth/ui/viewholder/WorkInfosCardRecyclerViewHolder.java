package com.example.bookmoth.ui.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmoth.R;

public class WorkInfosCardRecyclerViewHolder extends RecyclerView.ViewHolder {
    public ImageView cover;
    public TextView author;
    public TextView title;
    public TextView pdate;
    public TextView viewcount;
    public TextView price;
    public TextView desc;

    public WorkInfosCardRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        cover = itemView.findViewById(R.id.wkdt_img_cover);
        author = itemView.findViewById(R.id.wkdt_tv_author);
        title = itemView.findViewById(R.id.wkdt_tv_title);
        pdate = itemView.findViewById(R.id.wkdt_tv_pdate);
        viewcount = itemView.findViewById(R.id.wkdt_tv_view);
        price = itemView.findViewById(R.id.wkdt_tv_price);
        desc = itemView.findViewById(R.id.wkdt_tv_desc);
    }
}
