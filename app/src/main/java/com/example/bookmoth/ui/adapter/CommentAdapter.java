package com.example.bookmoth.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmoth.R;
import com.example.bookmoth.data.repository.post.SupabaseRepositoryImpl;
import com.example.bookmoth.domain.model.post.Comment;
import com.example.bookmoth.domain.usecase.post.PostUseCase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder>{
    private final Context context;
    private final List<Comment> commentList;
    private long lastClickTime = 0;
    private static final long CLICK_DELAY = 500;
    private final PostUseCase postUseCase = new PostUseCase(new SupabaseRepositoryImpl());

    public CommentAdapter(Context context,List<Comment> commentList){
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.tvContent.setText(comment.getContent());
        holder.tvTimestamp.setText(comment.getTime());
        holder.countLike.setText(String.valueOf(comment.getCount_like()));
        checkLikeComment(comment.getId(),holder);
        holder.btnLike.setOnClickListener(view -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastClickTime > CLICK_DELAY) {
                lastClickTime = currentTime;
                if (holder.isLiked) {
                    removeLike(comment.getId(), holder);
                } else {
                    addLike(comment.getId(), holder);
                }
            }
        });
    }

    private void checkLikeComment(int id, CommentViewHolder holder) {
        String user_id = "1";
        postUseCase.checkLikeComment("eq." + id, "eq." + user_id, "*").enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Log.d("Like", "Người dùng đã like bình luận");
                    holder.isLiked = true;
                    holder.btnLike.setImageResource(R.drawable.button_liked);
                } else {
                    Log.d("Like", "Người dùng chưa like comment");
                    holder.isLiked = false;
                    holder.btnLike.setImageResource(R.drawable.button_like);
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                Log.e("Like", "Lỗi kiểm tra like", t);
            }
        });
    }

    private void addLike(int commentID, CommentViewHolder holder) {
        Map<String, Object> body = new HashMap<>();
        body.put("id_comment", commentID);
        body.put("id_user", 1);  // Cần sửa nếu có userId thực tế
//       body.put("user_id", getCurrentUserId());

        holder.isLiked = true;
        String likeText = holder.countLike.getText().toString().trim();
        int currentLikeCount = likeText.isEmpty() ? 0 : Integer.parseInt(likeText);
        int newLikeCount = currentLikeCount + 1;
        holder.btnLike.setImageResource(R.drawable.button_liked);

        // Gửi request đến API
        postUseCase.addLikeComment(body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    Log.e("Like", "Lỗi thêm like: " + response.errorBody());
                    rollbackLikeUI(holder, currentLikeCount, false);
                }
                else {
                    updateLikeCount(commentID, newLikeCount);
                    holder.countLike.setText(String.valueOf(newLikeCount));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Like", "Lỗi kết nối khi thêm like", t);
                rollbackLikeUI(holder, currentLikeCount, false);
            }
        });
    }

    public void removeLike(int idComment, CommentViewHolder holder){
        String userId = "1";
        String url = "https://vhqcdiaoqrlcsnqvjpqh.supabase.co/rest/v1/likecomment?"+ "id_comment=eq."+idComment+ "&id_user=eq."+userId;
        holder.isLiked = false;
        String likeText = holder.countLike.getText().toString().trim();
        int currentLikeCount = likeText.isEmpty() ? 0 : Integer.parseInt(likeText);
        int newLikeCount = currentLikeCount - 1;
        holder.btnLike.setImageResource(R.drawable.button_like);
        postUseCase.removeLikeComment(url).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    Log.e("Like", "Lỗi xóa like: " + response.errorBody());
                    rollbackLikeUI(holder, currentLikeCount, true);
                }
                else {
                    updateLikeCount(idComment, newLikeCount);
                    holder.countLike.setText(String.valueOf(newLikeCount));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Like", "Lỗi kết nối API: " + t.getMessage());
                holder.isLiked = true;
                holder.countLike.setText(String.valueOf(currentLikeCount));
                holder.btnLike.setImageResource(R.drawable.button_liked);
            }
        });
    }

    private void rollbackLikeUI(CommentViewHolder holder, int likeCount, boolean isLiked) {
        holder.isLiked = isLiked;
        holder.countLike.setText(String.valueOf(likeCount));
        holder.btnLike.setImageResource(isLiked ? R.drawable.button_liked : R.drawable.button_like);
    }
    private void updateLikeCount(int commentId, int newLikeCount) {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("count_like", newLikeCount);

        postUseCase.updateLikeComment("eq." + commentId, updateData)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Log.d("API", "Cập nhật like thành công!");
                        } else {
                            Log.e("API", "Lỗi cập nhật like: " + response.errorBody());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("API", "Lỗi kết nối khi cập nhật like", t);
                    }
                });
    }
    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvContent, tvTimestamp,countLike;
        ImageButton btnLike;
        boolean isLiked = false;
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            countLike = itemView.findViewById(R.id.count_like);
            btnLike = itemView.findViewById(R.id.button_like);
        }
    }
}
