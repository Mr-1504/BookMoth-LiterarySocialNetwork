package com.example.bookmoth.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmoth.R;

import java.util.List;

public class TextItemRecyclerViewAdapter extends RecyclerView.Adapter<TextItemRecyclerViewAdapter.ViewHolder>{
    private List<String> texts;
    private OnItemClickListener listener;

    public TextItemRecyclerViewAdapter(List<String> texts, OnItemClickListener listener) {
        this.texts = texts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview_simpletext, parent, false);
        return new ViewHolder(v, listener) {
        };
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.text.setText(texts.get(position));
    }

    @Override
    public int getItemCount() {
        return texts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            text = itemView.findViewById(R.id.item_text);
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
}
