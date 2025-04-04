package com.example.bookmoth.ui.adapter.shop;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmoth.R;
import com.example.bookmoth.domain.model.shop.Category;

import java.util.List;

public class Category_Adapter extends RecyclerView.Adapter<Category_Adapter.ViewHolder> {
    private List<Category> categories;
    private Context context;
    private OnCategoryClickListener onCategoryClickListener;

    public Category_Adapter(List<Category> categories, Context context, OnCategoryClickListener onCategoryClickListener) {
        this.categories = categories;
        this.context = context;
        this.onCategoryClickListener = onCategoryClickListener;

        Log.d("ADAPTER_INIT", "Số lượng thể loại trong adapter: " + this.categories.size());
        for (Category category : this.categories) {
            Log.d("ADAPTER_INIT", "Thể loại trong adapter: " + category.getTag());
        }
    }

    public interface OnCategoryClickListener{
        void onCategoryClick(Category category);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.tvCategoryTag.setText(category.getTag());
        holder.itemView.setOnClickListener(v -> onCategoryClickListener.onCategoryClick(category));
        Log.d("Category_Adapter", "Binding item at position " + position + ": " + category.getTag());
    }

    @Override
    public int getItemCount() {
        return (categories != null) ? categories.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvCategoryTag;
        ViewHolder(View itemView){
            super(itemView);
            tvCategoryTag = (TextView) itemView.findViewById(R.id.category_text);
        }
    }
}
