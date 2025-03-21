package com.example.bookmoth.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.bookmoth.R;
import com.example.bookmoth.domain.model.post.Book;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private List<Book> bookList;
    private Context context;
    private OnBookClickListener listener;

    public BookAdapter(Context context, List<Book> bookList, OnBookClickListener listener) {
        this.context = context;
        this.bookList = bookList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.bookTitle.setText(book.getTitle());

        Glide.with(context)
                .load(book.getCover_url())
                .placeholder(R.drawable.placeholder_image)
                .diskCacheStrategy(DiskCacheStrategy.NONE)  // Không lưu cache
                .timeout(10_000)
                .override(512,512)
                .thumbnail(0.4f)
                .skipMemoryCache(true)  // Bỏ qua cache RAM
                .into(holder.bookCover);
        Log.d("BOOK_ADAPTER", "Book Title: " + book.getTitle() + ", work_id: " + book.getWorks_id());
        holder.itemView.setOnClickListener(v -> listener.onBookClick(book.getWorks_id()));
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView bookTitle;
        ImageView bookCover;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            bookTitle = itemView.findViewById(R.id.textTitleSach);
            bookCover = itemView.findViewById(R.id.imageBooks);
        }
    }
    public interface OnBookClickListener {
        void onBookClick(int workId);
    }
}

