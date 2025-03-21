package com.example.bookmoth.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmoth.R;
import com.example.bookmoth.core.utils.SecureStorage;
import com.example.bookmoth.data.remote.post.Api;
import com.example.bookmoth.data.remote.post.ApiResponse;
import com.example.bookmoth.data.repository.post.SupabaseRepositoryImpl;
import com.example.bookmoth.data.repository.profile.ProfileRepositoryImpl;
import com.example.bookmoth.domain.model.post.Comment;
import com.example.bookmoth.domain.model.post.Profile;
import com.example.bookmoth.domain.usecase.post.FlaskUseCase;
import com.example.bookmoth.domain.usecase.post.PostUseCase;
import com.example.bookmoth.domain.usecase.profile.ProfileUseCase;
import com.example.bookmoth.ui.viewmodel.profile.ProfileViewModel;
import com.squareup.picasso.Picasso;

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
    private String profileId;
    private FlaskUseCase flaskUseCase;

    public CommentAdapter(Context context,List<Comment> commentList,  FlaskUseCase flaskUseCase){
        this.context = context;
        this.commentList = commentList;
        this.flaskUseCase = flaskUseCase;
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
        getNameProfile(comment.getUser_id(), new PostAdapter.NameCallback() {
            @Override
            public void onSuccess(String name) {
                Log.d("Profile Name", "Tên người dùng: " + name);
                holder.nameProfile.setText(name);
            }

            @Override
            public void onError(String error) {
                Log.e("Profile API", error);
            }
        });


        getImageProfile(comment.getUser_id(), new PostAdapter.ImageCallback() {
            @Override
            public void onSuccess(String imageUrl) {
                Picasso.get().load(imageUrl).into(holder.btnProfile);
                Log.e("Profile Image", "Ảnh đại diện: " + imageUrl);
            }

            @Override
            public void onError(String error) {
                Log.e("Profile API", error);
            }
        });
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

    private void getProfile() {
        ProfileViewModel profileViewModel = new ProfileViewModel(
                new ProfileUseCase(new ProfileRepositoryImpl())
        );

        profileViewModel.getProfile(context, new ProfileViewModel.OnProfileListener() {
            @Override
            public void onProfileSuccess(com.example.bookmoth.domain.model.profile.Profile profile) {
                SecureStorage.saveToken("profileId", profile.getProfileId());
                profileId = profile.getProfileId();
            }

            @Override
            public void onProfileFailure(String error) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErrorConnectToServer(String error) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getImageProfile(int authorId, PostAdapter.ImageCallback callback) {
        flaskUseCase.getProfileAvata(authorId).enqueue(new Callback<Api>() {
            @Override
            public void onResponse(Call<Api> call, Response<Api> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String imageUrl = response.body().getData();
                    callback.onSuccess(imageUrl);
                } else {
                    callback.onError("Lỗi lấy ảnh profile: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Api> call, Throwable t) {
                callback.onError("Lỗi kết nối API: " + t.getMessage());
            }
        });
    }


    // Interface callback cho ảnh đại diện
    public interface ImageCallback {
        void onSuccess(String imageUrl);
        void onError(String error);
    }

    private void getNameProfile(int authorId, PostAdapter.NameCallback callback) {
        flaskUseCase.getProfile(authorId).enqueue(new Callback<ApiResponse<Profile>>() {
            @Override
            public void onResponse(Call<ApiResponse<Profile>> call, Response<ApiResponse<Profile>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Profile profile = response.body().getData();
                    String fullName = profile.getFirstName() + " " + profile.getLastName();
                    callback.onSuccess(fullName);
                } else {
                    callback.onError("Lỗi lấy thông tin profile: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Profile>> call, Throwable t) {
                callback.onError("Lỗi kết nối API: " + t.getMessage());
            }
        });
    }


    // Interface để callback kết quả
    public interface NameCallback {
        void onSuccess(String name);
        void onError(String error);
    }
    private void checkLikeComment(int id, CommentViewHolder holder) {
        profileId = SecureStorage.getToken("profileId");
        getProfile();
        String user_id = profileId;
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
        profileId = SecureStorage.getToken("profileId");
        getProfile();
        body.put("id_user", Integer.parseInt(profileId));  // Cần sửa nếu có userId thực tế
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
        profileId = SecureStorage.getToken("profileId");
        getProfile();
        String userId = profileId;
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
        TextView tvContent, tvTimestamp,countLike, nameProfile;
        ImageButton btnProfile;

        ImageButton btnLike;
        boolean isLiked = false;
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            countLike = itemView.findViewById(R.id.count_like);
            btnLike = itemView.findViewById(R.id.button_like);
            btnProfile = itemView.findViewById(R.id.imageProfile);
            nameProfile = itemView.findViewById(R.id.nameProfile);
        }
    }
}
