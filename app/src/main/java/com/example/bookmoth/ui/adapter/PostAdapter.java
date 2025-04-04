package com.example.bookmoth.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookmoth.R;
import com.example.bookmoth.core.utils.SecureStorage;
import com.example.bookmoth.data.local.post.PostSQLiteHelper;
import com.example.bookmoth.data.model.profile.ProfileDatabase;
import com.example.bookmoth.data.remote.post.ApiResponse;
import com.example.bookmoth.data.repository.profile.LocalProfileRepositoryImpl;
import com.example.bookmoth.data.repository.profile.ProfileRepositoryImpl;
import com.example.bookmoth.domain.model.post.Post;
import com.example.bookmoth.domain.model.post.Profile;
import com.example.bookmoth.domain.usecase.post.FlaskUseCase;
import com.example.bookmoth.domain.usecase.post.PostUseCase;
import com.example.bookmoth.domain.usecase.profile.ProfileUseCase;
import com.example.bookmoth.ui.activity.post.CommentActivity;
import com.example.bookmoth.ui.activity.post.EditPostActivity;
import com.example.bookmoth.ui.activity.profile.ProfileActivity;
import com.example.bookmoth.ui.viewmodel.profile.ProfileViewModel;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private final Context context;
    private final List<Post> postList;
    private MediaPlayer mediaPlayer;
    private long lastClickTime = 0;
    private PostUseCase postUseCase;
    private FlaskUseCase flaskUseCase;
    private static final long CLICK_DELAY = 0;
    private String profileId;
    public PostAdapter(Context context, List<Post> postList, PostUseCase postUseCase, FlaskUseCase flaskUseCase) {
        this.context = context;
        this.postList = postList;
        this.mediaPlayer = new MediaPlayer();
        this.postUseCase = postUseCase;
        this.flaskUseCase = flaskUseCase;
    }

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
            }
        });
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.tvTitle.setText(post.getTitle());
        holder.tvContent.setText(post.getContent());
        holder.tvTimestamp.setText(post.getTimestamp());
        holder.countLike.setText(String.valueOf(post.getCount_like()));
        holder.countComment.setText(String.valueOf(post.getCount_comment()));

        if(post.getTab_works() == -1){
            holder.tabWorks.setVisibility(View.GONE);
        }
        else{
            holder.tabWorks.setVisibility(View.VISIBLE);
            holder.tabWorks.setText(String.valueOf(post.getTab_works()));
        }
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileId = SecureStorage.getToken("profileId");
                getProfile();
                if(!Objects.equals(profileId, String.valueOf(post.getAuthorId()))){
                    Toast.makeText(context, "Bạn không có quyền xóa bài viết này", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    holder.btnDelete.setEnabled(false);
                    Map<String, Object> body = new HashMap<>();
                    body.put("status", 1);
                    Call<ResponseBody> call = postUseCase.updatePostStatus("eq."+post.getPostId(), body);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            holder.btnDelete.setEnabled(true);
                            if (response.isSuccessful()) {
                                int currentPosition = holder.getAdapterPosition();
                                if (currentPosition != RecyclerView.NO_POSITION) {
                                    postList.remove(currentPosition);
                                    notifyItemRemoved(currentPosition);
                                    notifyItemRangeChanged(currentPosition, postList.size());
                                }
                                Toast.makeText(context, "Xóa bài viết thành công", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Xóa thất bại: " + response.message(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            holder.btnDelete.setEnabled(true);
                            Toast.makeText(context, "Lỗi kết nối API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileId = SecureStorage.getToken("profileId");
                getProfile();
                if(!Objects.equals(profileId, String.valueOf(post.getAuthorId()))){
                    Toast.makeText(context, "Bạn không có quyền sửa bài viết này", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    Intent intent = new Intent(context, EditPostActivity.class);
                    intent.putExtra("postID", post.getPostId());
                    context.startActivity(intent);
                }
            }
        });


        PostSQLiteHelper dbHelper = new PostSQLiteHelper(context);

        // Tên đại diện
        if (isNetworkAvailable()) {
            getNameProfile(post.getAuthorId(), new NameCallback() {
                @Override
                public void onSuccess(String name) {
                    holder.nameProfile.setText(name);
                    post.setAuthor_name(name); // Cập nhật để lưu vào SQLite sau này
                }

                @Override
                public void onError(String error) {
                    // Khi API lỗi, lấy từ SQLite
                    String localName = dbHelper.getAuthorNameById(post.getAuthorId());
                    holder.nameProfile.setText(localName != null ? localName : "Unknown");
                    post.setAuthor_name(localName); // Cập nhật post nếu cần
                }
            });
        } else {
            // Khi không có mạng, lấy trực tiếp từ SQLite
            String localName = dbHelper.getAuthorNameById(post.getAuthorId());
            holder.nameProfile.setText(localName != null ? localName : "Unknown");
            post.setAuthor_name(localName); // Cập nhật post nếu cần
        }
        // Ảnh đại diện (dùng đường dẫn file từ SQLite)
        String avatarPath = post.getAuthor_avatar_url();
        if (avatarPath != null && !avatarPath.isEmpty() && new File(avatarPath).exists()) {
            Picasso.get()
                    .load(new File(avatarPath))
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .into(holder.btnProfile);
        } else {
            Picasso.get()
                    .load(R.drawable.avatar)
                    .into(holder.btnProfile);
        }

//        getNameProfile(post.getAuthorId(), new NameCallback() {
//            @Override
//            public void onSuccess(String name) {
//                Log.d("Profile Name", "Tên người dùng: " + name);
//                holder.nameProfile.setText(name);
//            }
//
//            @Override
//            public void onError(String error) {
//                Log.e("Profile API", error);
//            }
//        });

//        String imageUrl = "http://127.0.0.1:7100/images/avatars/"+ post.getAuthorId()+".pnj";
//        Picasso.get().load(imageUrl).error(R.drawable.avatar).into(holder.btnProfile);

        holder.btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("profileId", post.getAuthorId());
                context.startActivity(intent);
            }
        });
//        getImageProfile(post.getAuthorId(), new ImageCallback() {
//            @Override
//            public void onSuccess(String imageUrl) {
//                Picasso.get().load(imageUrl).into(holder.btnProfile);
//                Log.e("Profile Image", "Ảnh đại diện: " + imageUrl);
//            }
//
//            @Override
//            public void onError(String error) {
//                Log.e("Profile API", error);
//            }
//        });

        checkLikeStatus(post.getPostId(), holder);
        holder.btnLike.setOnClickListener(v -> {
            if (holder.isLiked) {
                removeLike(post.getPostId(), holder);
            } else {
                addLike(post.getPostId(), holder);
            }
//            long currentTime = System.currentTimeMillis();
//            if (currentTime - lastClickTime > CLICK_DELAY) {
//                lastClickTime = currentTime;
//
//            }
        });
        holder.btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CommentActivity.class);
                intent.putExtra("postID", post.getPostId());
                view.getContext().startActivity(intent);
            }
        });
        Log.d("Adapter", "Binding post: " + post.getTitle());

        String mediaUrl = post.getMediaUrl();
        holder.resetMediaViews(); // Ẩn tất cả trước khi hiển thị nội dung phù hợp

        if(isNetworkAvailable()){
            if (mediaUrl != null && !mediaUrl.isEmpty()) {
                if (mediaUrl.endsWith(".jpg") || mediaUrl.endsWith(".png") || mediaUrl.endsWith(".jpeg")) {
                    holder.imageView.setVisibility(View.VISIBLE);
                    Glide.with(context)
                            .load(mediaUrl)
                            .placeholder(R.drawable.placeholder_image) // Hiển thị ảnh mặc định khi tải
                            .error(R.drawable.error_image) // Hiển thị ảnh lỗi nếu tải thất bại
                            .into(holder.imageView);
                }
            }
        }
        else{
            if (mediaUrl != null && !mediaUrl.isEmpty()) {
                if (mediaUrl.endsWith(".jpg") || mediaUrl.endsWith(".png") || mediaUrl.endsWith(".jpeg")) {
                    holder.imageView.setVisibility(View.VISIBLE);
                    Glide.with(context)
                            .load(new File(mediaUrl))
                            .placeholder(R.drawable.placeholder_image) // Hiển thị ảnh mặc định khi tải
                            .error(R.drawable.error_image) // Hiển thị ảnh lỗi nếu tải thất bại
                            .into(holder.imageView);
                }
            }
        }

        clickOpenProfile(holder, post.getAuthorId());

    }

    private void clickOpenProfile(PostViewHolder holder, int authorId) {
        holder.imageView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProfileActivity.class);
            intent.putExtra("profileId", authorId);
            context.startActivity(intent);
        });

        holder.nameProfile.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProfileActivity.class);
            intent.putExtra("profileId", authorId);
            context.startActivity(intent);
        });
    }
//    private void getImageProfile(int authorId, ImageCallback callback) {
//        flaskUseCase.getProfileAvata(authorId).enqueue(new Callback<Api>() {
//            @Override
//            public void onResponse(Call<Api> call, Response<Api> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    String imageUrl = response.body().getData();
//                    callback.onSuccess(imageUrl);
//                } else {
//                    callback.onError("Lỗi lấy ảnh profile: " + response.errorBody());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Api> call, Throwable t) {
//                callback.onError("Lỗi kết nối API: " + t.getMessage());
//            }
//        });
//    }


    // Interface callback cho ảnh đại diện
    public interface ImageCallback {
        void onSuccess(String imageUrl);
        void onError(String error);
    }

    private void getNameProfile(int authorId, NameCallback callback) {
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

    private void checkLikeStatus(int postId, PostViewHolder holder) {
        profileId = SecureStorage.getToken("profileId");
        getProfile();
        int userId = Integer.parseInt(profileId); // Cần lấy userId thực tế từ session hoặc database
        postUseCase.checkLikeStatus("eq." + postId, "eq." + userId, "*")
                .enqueue(new Callback<List<Map<String, Object>>>() {
                    @Override
                    public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            Log.d("Like", "Người dùng đã like bài viết");
                            holder.isLiked = true;
                            holder.btnLike.setImageResource(R.drawable.button_liked);
                        } else {
                            Log.d("Like", "Người dùng chưa like bài viết");
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

    private void toggleLike(int postId, PostViewHolder holder, boolean isAddingLike) {
        String profileId = SecureStorage.getToken("profileId");
        getProfile();

        getCurrentLikeCount(postId, new LikeCountCallback() {
            @Override
            public void onLikeCountReceived(int currentLikeCount) {
                int likeChange = isAddingLike ? 1 : -1;
                int updatedLikeCount = Math.max(0, currentLikeCount + likeChange);
                holder.countLike.setText(String.valueOf(updatedLikeCount));
                holder.isLiked = isAddingLike;
                holder.btnLike.setImageResource(isAddingLike ? R.drawable.button_liked : R.drawable.button_like);

                if (isAddingLike) {
                    Map<String, Object> body = new HashMap<>();
                    body.put("post_id", postId);
                    body.put("user_id", profileId);
                    postUseCase.addLike(body).enqueue(new LikeCallback(holder, postId, currentLikeCount, isAddingLike));
                } else {
                    String url = "https://vhqcdiaoqrlcsnqvjpqh.supabase.co/rest/v1/likes?post_id=eq." + postId + "&user_id=eq." + profileId;
                    postUseCase.removeLike(url).enqueue(new LikeCallback(holder, postId, currentLikeCount, isAddingLike));
                }
            }
        });
    }

    private class LikeCallback implements Callback<ResponseBody> {
        private final PostViewHolder holder;
        private final int postId;
        private final int previousLikeCount;
        private final boolean isAddingLike;

        public LikeCallback(PostViewHolder holder, int postId, int previousLikeCount, boolean isAddingLike) {
            this.holder = holder;
            this.postId = postId;
            this.previousLikeCount = previousLikeCount;
            this.isAddingLike = isAddingLike;
        }

        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            if (!response.isSuccessful()) {
                Log.e("Like", "Lỗi khi cập nhật like: " + response.errorBody());
                rollbackLikeUI();
            } else {
                updateLikeCount(postId, Math.max(0, previousLikeCount + (isAddingLike ? 1 : -1)));
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            Log.e("Like", "Lỗi kết nối API: " + t.getMessage());
            rollbackLikeUI();
        }

        private void rollbackLikeUI() {
            holder.isLiked = !isAddingLike;
            holder.btnLike.setImageResource(isAddingLike ? R.drawable.button_like : R.drawable.button_liked);
            holder.countLike.setText(String.valueOf(Math.max(0, previousLikeCount)));
        }
    }

    public void addLike(int postId, PostViewHolder holder) {
        toggleLike(postId, holder, true);
    }

    public void removeLike(int postId, PostViewHolder holder) {
        toggleLike(postId, holder, false);
    }

    private void getCurrentLikeCount(int postID, LikeCountCallback callback) {
        postUseCase.getLikeForId("eq." + postID).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int likeCount = response.body();
                    callback.onLikeCountReceived(likeCount);
                } else {
                    callback.onLikeCountReceived(0);
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                callback.onLikeCountReceived(0);
            }
        });
    }


    private void updateLikeCount(int postId, int newLikeCount) {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("count_like", newLikeCount);

        postUseCase.updateLike("eq." + postId, updateData)
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




    private void playAudio(String audioUrl) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }

        try {
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(MediaPlayer::start);
        } catch (IOException e) {
            Log.e("Adapter", "Error playing audio", e);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvTimestamp,countLike, countComment, tabWorks, nameProfile;
        ImageView imageView;
        ImageButton btnLike, btnComment, btnProfile, btnDelete, btnEdit;
        boolean isLiked = false;
//        VideoView videoView;
//        Button btnPlayAudio;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            imageView = itemView.findViewById(R.id.imageView);
            btnComment = itemView.findViewById(R.id.button_comment);
            btnLike = itemView.findViewById(R.id.button_like);
            countLike = itemView.findViewById(R.id.countLike);
            countComment = itemView.findViewById(R.id.countComment);
            tabWorks = itemView.findViewById(R.id.tvTabWorks);
            btnProfile = itemView.findViewById(R.id.imageProfile);
            nameProfile = itemView.findViewById(R.id.nameProfile);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
//            videoView = itemView.findViewById(R.id.videoView);
//            btnPlayAudio = itemView.findViewById(R.id.btnPlayAudio);

        }

        public void resetMediaViews() {
            imageView.setVisibility(View.GONE);
//            videoView.setVisibility(View.GONE);
//            btnPlayAudio.setVisibility(View.GONE);
        }
    }
}
