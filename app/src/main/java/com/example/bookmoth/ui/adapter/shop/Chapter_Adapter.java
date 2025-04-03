package com.example.bookmoth.ui.adapter.shop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmoth.R;
import com.example.bookmoth.domain.model.shop.Chapter;
import com.example.bookmoth.ui.activity.shop.OnChapterClickListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Chapter_Adapter extends RecyclerView.Adapter<Chapter_Adapter.ChapterViewHolder> {
    private List<Chapter> chapterList;

    private OnChapterClickListener clickListener;

    public Chapter_Adapter(List<Chapter> chapterList) {
        this.chapterList = chapterList;
    }

    public void setOnChapterClickListener(OnChapterClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chapter,parent,false);
        return new ChapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        Chapter chapter = chapterList.get(position);
        holder.tvChapterTitle.setText(chapter.getTitle());
        holder.tvPostDate.setText(formatDate(chapter.getPost_date()));

        holder.itemView.setOnClickListener(v -> {
            if(clickListener != null)  {
                clickListener.onChapterClick(chapter);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chapterList.size();
    }

    public static class ChapterViewHolder extends RecyclerView.ViewHolder {
        TextView tvChapterTitle, tvPostDate;
        public ChapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvChapterTitle = itemView.findViewById(R.id.tv_chapter_title);
            tvPostDate = itemView.findViewById(R.id.tv_post_date);
        }
    }
    private  String formatDate(String postDateStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
            SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, dd-MM-yyyy", new Locale("vi", "VN"));
            Date date = inputFormat.parse(postDateStr);
            return  outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return postDateStr;
        }
    }
}
