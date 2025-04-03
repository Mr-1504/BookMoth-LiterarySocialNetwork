package com.example.bookmoth.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookmoth.R;
import com.example.bookmoth.core.libraryutils.AppConst;
import com.example.bookmoth.core.libraryutils.DateTimeFormat;
import com.example.bookmoth.domain.model.library.Chapter;
import com.example.bookmoth.domain.model.library.Work;
import com.example.bookmoth.ui.viewholder.ChapterItemRecyclerViewHolder;
import com.example.bookmoth.ui.viewholder.WorkDashboardActionRecyclerViewHolder;
import com.example.bookmoth.ui.viewholder.WorkDashboardStatsRecyclerViewHolder;

import java.util.List;

public class WorkDashboardRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static int VIEWTYPE_STATS = 0;
    private static int VIEWTYPE_QUICKACTION = 1;
    private static int VIEWTYPE_CHAPTERS = 2;

    private OnItemClickListener addChapter = null;
    private OnItemClickListener deleteWork = null;
    private OnItemClickListener editWork = null;
    private OnItemClickListener chapterClick = null;

    private Work work;
    private List<Chapter> chapters;

    public WorkDashboardRecyclerViewAdapter(Work work, List<Chapter> chapters) {
        this.work = work;
        this.chapters = chapters;
    }

    public void attachQuickActionListener(OnItemClickListener addChapter, OnItemClickListener deleteWork, OnItemClickListener editWork) {
        this.addChapter = addChapter;
        this.deleteWork = deleteWork;
        this.editWork = editWork;
    }

    public void attachChapterClickListener(OnItemClickListener chapterClick) {
        this.chapterClick = chapterClick;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEWTYPE_STATS : position == 1 ? VIEWTYPE_QUICKACTION : VIEWTYPE_CHAPTERS;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEWTYPE_STATS) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_obj_workdash_stats, parent, false);
            return new WorkDashboardStatsRecyclerViewHolder(v);
        } else if (viewType == VIEWTYPE_QUICKACTION) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workdash_quickaction, parent, false);
            return new WorkDashboardActionRecyclerViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chapter_list, parent, false);
            return new ChapterItemRecyclerViewHolder(v, chapterClick);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof WorkDashboardStatsRecyclerViewHolder) {
            WorkDashboardStatsRecyclerViewHolder hdr = (WorkDashboardStatsRecyclerViewHolder) holder;
            if (work.getCover_url() != null && !work.getCover_url().isBlank()) {
                Glide.with(hdr.cover)
                        .load(AppConst.API_URL + AppConst.CDN_COVER + work.getCover_url() + "?v=" + System.currentTimeMillis())
                        .into(hdr.cover);
            }
            hdr.author.setText(work.getAuthor_name());
            hdr.title.setText(work.getTitle());
        }
        else if (holder instanceof WorkDashboardActionRecyclerViewHolder) {
            WorkDashboardActionRecyclerViewHolder hdr = (WorkDashboardActionRecyclerViewHolder) holder;
            hdr.fl_addChapter.setOnClickListener(v -> addChapter.onItemClick(work.getWork_id()));
            hdr.fl_deleteWork.setOnClickListener(v -> deleteWork.onItemClick(work.getWork_id()));
            hdr.fl_editWork.setOnClickListener(v -> editWork.onItemClick(work.getWork_id()));
        }
        else if (holder instanceof ChapterItemRecyclerViewHolder) {
            ChapterItemRecyclerViewHolder hdr = (ChapterItemRecyclerViewHolder) holder;
            hdr.arrayIndex = (chapters.size() - 1) - (position - 2);
            Chapter item = chapters.get(hdr.arrayIndex);
            hdr.index.setText(String.valueOf(chapters.indexOf(item)+1));
            hdr.pdate.setText(DateTimeFormat.format(item.getPost_date(), DateTimeFormat.DATE_TIME));
            hdr.title.setText(item.getTitle());
        }
    }

    @Override
    public int getItemCount() {
        return chapters.size() + 2;
    }
}
