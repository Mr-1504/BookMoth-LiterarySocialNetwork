package com.example.bookmoth.ui.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookmoth.R;
import com.example.bookmoth.core.libraryutils.LibraryConst;
import com.example.bookmoth.domain.model.library.Work;
import com.example.bookmoth.ui.viewholder.RefreshBarTitleRecyclerViewHolder;
import com.example.bookmoth.ui.viewholder.WorkItemRecyclerViewHolder;

import java.util.List;

public class ReaderPageRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static int VIEWTYPE_HEADER = 0;
    private static int VIEWTYPE_ITEM = 1;
    private static int VIEWTYPE_ENDITEM = 2;

    private List<Work> works;
    private OnItemClickListener itemListener;
    private View.OnClickListener refreshListener;
    private int dpPadding = 1;

    public ReaderPageRecyclerViewAdapter(List<Work> works, OnItemClickListener itemListener, View.OnClickListener refreshListener, int dpPadding) {
        this.works = works;
        this.itemListener = itemListener;
        this.refreshListener = refreshListener;
        this.dpPadding = dpPadding;
    }

    @Override
    public int getItemViewType(int position) {
        Log.d("READERPOS", String.valueOf(position));
        return position == 0 ? VIEWTYPE_HEADER : position == works.size() ? VIEWTYPE_ENDITEM : VIEWTYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEWTYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_header_titlewithrefresh, parent, false);
            return new RefreshBarTitleRecyclerViewHolder(v, refreshListener);
        }
        else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_work_card, parent, false);
            if (viewType == VIEWTYPE_ENDITEM) v.setPadding(0, 0, 0, (int) (dpPadding * parent.getResources().getDisplayMetrics().density));
            return new WorkItemRecyclerViewHolder(v, itemListener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RefreshBarTitleRecyclerViewHolder) {
            RefreshBarTitleRecyclerViewHolder hdr = (RefreshBarTitleRecyclerViewHolder) holder;
            hdr.title.setText(hdr.itemView.getContext().getString(R.string.libmode_title_reader));
        } else if (holder instanceof WorkItemRecyclerViewHolder) {
            WorkItemRecyclerViewHolder hdr = (WorkItemRecyclerViewHolder) holder;
            Work item = works.get(position - 1);

            hdr.title.setText(String.valueOf(item.getTitle()));
            hdr.author.setText(String.valueOf(item.getAuthor_name()));
            if (item.getCover_url() != null && !item.getCover_url().equalsIgnoreCase("null"))
                Glide.with(holder.itemView)
                        .load(LibraryConst.API_URL + LibraryConst.CDN_COVER + item.getCover_url() + "?v=" + System.currentTimeMillis())
                        .into(hdr.cover);
        }
    }

    @Override
    public int getItemCount() {
        return works.size() + 1;
    }
}
