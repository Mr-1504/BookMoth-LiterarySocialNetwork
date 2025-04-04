package com.example.bookmoth.ui.adapter.shop;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.bookmoth.R;
import com.example.bookmoth.domain.model.shop.Work;
import com.example.bookmoth.ui.activity.shop.WorkDetailActivity;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class Work_Adapter extends RecyclerView.Adapter<Work_Adapter.WorkViewHolder> {
    private List<Work> works;
    private Context context;

    @NonNull
    @Override
    public WorkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("WorkAdapter", "Creating ViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_work, parent, false);
        return new WorkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkViewHolder holder, int position) {
        if (works == null || works.isEmpty()) {
            Log.w("WorkAdapter", "Works list is empty, cannot bind item at position " + position);
            return;
        }

        if (position < 0 || position >= works.size()) {
            Log.w("WorkAdapter", "Invalid position: " + position + ", works size: " + works.size());
            return;
        }

        Work work = works.get(position);
        Log.d("WorkAdapter", "Binding item at position " + position + ": " + work.getTitle());
        Log.d("WorkAdapter", "Loading image for " + work.getTitle() + ": " + work.getCover_url());
        Glide.with(holder.itemView.getContext())
                .load(work.getCover_url())
                .thumbnail(0.25f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(100,150)
                .into(holder.ivWorkCover);
        holder.tvWorkTitle.setText(work.getTitle());
        holder.tvWorkViewCount.setText(work.getView_count().toString());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, WorkDetailActivity.class);
            intent.putExtra("work", work);
            context.startActivity(intent);
        });
    }

    public Work_Adapter(List<Work> works, Context context) {
        this.works = works;
        this.context = context;
        Log.d("WorkAdapter", "WorkAdapter initialized with " + (works != null ? works.size() : 0) + " items");

    }

    @Override
    public int getItemCount() {
        return works != null ? works.size() : 0;
    }

    public static class WorkViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView ivWorkCover;
        TextView tvWorkTitle, tvWorkViewCount;

        public WorkViewHolder(@NonNull View itemView) {
            super(itemView);
            ivWorkCover = itemView.findViewById(R.id.iv_work_cover);
            tvWorkTitle = itemView.findViewById(R.id.tv_work_title);
            tvWorkViewCount = itemView.findViewById(R.id.tv_work_view_count);
            Log.d("WorkAdapter", "WorkViewHolder initialized");
        }
    }


}
