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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmoth.R;
import com.example.bookmoth.core.utils.SecureStorage;
import com.example.bookmoth.data.local.profile.ProfileDatabase;
import com.example.bookmoth.data.remote.post.Api;
import com.example.bookmoth.data.remote.post.ApiResponse;
import com.example.bookmoth.data.repository.post.SupabaseRepositoryImpl;
import com.example.bookmoth.data.repository.profile.LocalProfileRepositoryImpl;
import com.example.bookmoth.data.repository.profile.ProfileRepositoryImpl;
import com.example.bookmoth.domain.model.post.Comment;
import com.example.bookmoth.domain.model.post.Profile;
import com.example.bookmoth.domain.usecase.post.FlaskUseCase;
import com.example.bookmoth.domain.usecase.post.PostUseCase;
import com.example.bookmoth.domain.usecase.profile.ProfileUseCase;
import com.example.bookmoth.ui.activity.post.CommentActivity;
import com.example.bookmoth.ui.viewmodel.post.PostViewModel;
import com.example.bookmoth.ui.viewmodel.post.SharedViewModel;
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
    private SharedViewModel viewModel;

    public CommentAdapter(Context context,List<Comment> commentList,  FlaskUseCase flaskUseCase, SharedViewModel viewModel){
        this.context = context;
        this.commentList = commentList;
        this.flaskUseCase = flaskUseCase;
        this.viewModel = viewModel;
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
            if (holder.isLiked) {
                removeLike(comment.getId(), holder);
            } else {
                addLike(comment.getId(), holder);
            }
        });
        holder.btnDeleteComment.setOnClickListener(view -> {

            profileId = SecureStorage.getToken("profileId");
            getProfile();
            if(profileId.equals(String.valueOf(comment.getUser_id()))){
                removeLikeComment(comment.getId());
                removeComment(comment.getId());
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    commentList.remove(currentPosition);
                    notifyItemRemoved(currentPosition);
                    notifyItemRangeChanged(currentPosition, commentList.size());
                }
                viewModel.setButtonClicked();
//                updateCountComment(comment.getPost_id());
            }
            else {
                Toast.makeText(context, "Bạn không thể xóa bình luận của người khác", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void removeLikeComment(int idComment){

        String url = "https://vhqcdiaoqrlcsnqvjpqh.supabase.co/rest/v1/likecomment?"+ "id_comment=eq."+idComment;

        postUseCase.removeLikeComment(url).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    Log.e("Like", "Lỗi xóa like: " + response.errorBody());
                }

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Like", "Lỗi kết nối API: " + t.getMessage());
            }
        });
    }
    private void removeComment(int idComment){
        String url = "https://vhqcdiaoqrlcsnqvjpqh.supabase.co/rest/v1/comments?"+ "id_comment=eq."+idComment;

        postUseCase.removeComment(url).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    Log.e("Like", "Lỗi xóa like: " + response.errorBody());
                }

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Like", "Lỗi kết nối API: " + t.getMessage());
            }
        });
    }
//    public void updateCountComment(int idPost) {
//        postUseCase.getCommentForId("eq."+idPost).enqueue(new Callback<List<Comment>>() {
//            @Override
//            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    int newCount = response.body().size();
//                    Map<String, Object> updateData = new HashMap<>();
//                    updateData.put("count_comment", newCount);
//                    postUseCase.updateComment("eq." + idPost, updateData).enqueue(new Callback<ResponseBody>() {
//                        @Override
//                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                            if (response.isSuccessful()) {
//                                Log.d("API", "Cập nhật số lượng comment thành công!");
//                            } else {
//                                Log.e("API", "Lỗi cập nhật số lượng comment: " + response.errorBody());
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<ResponseBody> call, Throwable t) {
//                            Log.e("API", "Lỗi kết nối khi cập nhật số lượng comment", t);
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Comment>> call, Throwable t) {
//                Log.e("API", "Lỗi kết nối khi lấy số lượng comment", t);
//            }
//        });
//    }
    private void getProfile() {
        LocalProfileRepositoryImpl localRepo = new LocalProfileRepositoryImpl(
                this.context, ProfileDatabase.getInstance(this.context).profileDao()
        );
        ProfileViewModel profileViewModel = new ProfileViewModel(
                new ProfileUseCase(localRepo, new ProfileRepositoryImpl())
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
        holder.countLike.setText(String.valueOf(newLikeCount));

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
        holder.countLike.setText(String.valueOf(newLikeCount));
        postUseCase.removeLikeComment(url).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    Log.e("Like", "Lỗi xóa like: " + response.errorBody());
                    rollbackLikeUI(holder, currentLikeCount, true);
                }
                else {
                    updateLikeCount(idComment, newLikeCount);

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

        ImageButton btnLike, btnDeleteComment;
        boolean isLiked = false;
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            countLike = itemView.findViewById(R.id.count_like);
            btnLike = itemView.findViewById(R.id.button_like);
            btnProfile = itemView.findViewById(R.id.imageProfile);
            nameProfile = itemView.findViewById(R.id.nameProfile);
            btnDeleteComment = itemView.findViewById(R.id.btnDeleteComment);
        }
    }
}
