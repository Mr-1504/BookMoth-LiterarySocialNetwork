package com.example.bookmoth.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookmoth.R;
import com.example.bookmoth.core.libraryutils.LibraryConst;
import com.example.bookmoth.domain.model.library.Work;
import com.example.bookmoth.ui.viewholder.AuthorQuickActionRecyclerViewHolder;
import com.example.bookmoth.ui.viewholder.RefreshBarTitleRecyclerViewHolder;
import com.example.bookmoth.ui.viewholder.WorkItemRecyclerViewHolder;

import java.util.List;

public class AuthorPageRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static int VIEWTYPE_HEADER = 0;
    public static int VIEWTYPE_QUICKACTION = 1;
    public static int VIEWTYPE_WORKITEM = 2;

    private List<Work> works;

    private View.OnClickListener refreshListener;
    private OnItemClickListener workClickListener;
    private OnItemClickListener addWorkListener;
    private OnItemClickListener addChapterListener;

    public AuthorPageRecyclerViewAdapter(List<Work> works) {
        this.works = works;
    }

    public void attachAddWorkListener(OnItemClickListener listener) {
        addWorkListener = listener;
    }

    public void attachAddChapteristener(OnItemClickListener listener) {
        addChapterListener = listener;
    }

    public void attachWorkClickListener(OnItemClickListener listener) {
        workClickListener = listener;
    }

    public void attachRefreshListener(View.OnClickListener listener) {refreshListener = listener;}

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEWTYPE_QUICKACTION) {
            return new AuthorQuickActionRecyclerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_author_quickaction, parent, false));
        } else if (viewType == VIEWTYPE_HEADER) {
            return new RefreshBarTitleRecyclerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_header_titlewithrefresh, parent, false), refreshListener);
        } else {
            return new WorkItemRecyclerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_work_card, parent, false), workClickListener);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEWTYPE_HEADER : position == 1 ? VIEWTYPE_QUICKACTION : VIEWTYPE_WORKITEM;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RefreshBarTitleRecyclerViewHolder) {
            RefreshBarTitleRecyclerViewHolder hdr = (RefreshBarTitleRecyclerViewHolder) holder;
            hdr.title.setText(hdr.itemView.getContext().getString(R.string.libmode_title_author));
            hdr.btn_refresh.setOnClickListener(refreshListener);
        } else
        if (holder instanceof AuthorQuickActionRecyclerViewHolder) {
            AuthorQuickActionRecyclerViewHolder hdr = (AuthorQuickActionRecyclerViewHolder) holder;
            hdr.btnNewWork.setOnClickListener(v -> addWorkListener.onItemClick(-1));
            hdr.btnNewChapter.setOnClickListener(V -> addChapterListener.onItemClick((-1)));
        } else
        if (holder instanceof WorkItemRecyclerViewHolder) {
            WorkItemRecyclerViewHolder hdr = (WorkItemRecyclerViewHolder) holder;
            Work item = works.get(position - 1);

            hdr.title.setText(item.getTitle());
            hdr.author.setText(item.getAuthor_name());
            Glide.with(hdr.cover)
                    .load(LibraryConst.API_URL + LibraryConst.CDN_COVER + item.getCover_url() + "?v=" + System.currentTimeMillis())
                    .into(hdr.cover);
        }
    }

    @Override
    public int getItemCount() {
        return works.size() + 2;
    }
}
