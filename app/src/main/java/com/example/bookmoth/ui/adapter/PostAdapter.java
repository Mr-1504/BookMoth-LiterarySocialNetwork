package com.example.bookmoth.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
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
import com.example.bookmoth.data.remote.post.Api;
import com.example.bookmoth.data.remote.post.ApiResponse;
import com.example.bookmoth.data.remote.post.SupabaseApiService;
import com.example.bookmoth.data.remote.utils.RetrofitClient;
import com.example.bookmoth.data.repository.profile.ProfileRepositoryImpl;
import com.example.bookmoth.domain.model.post.Post;
import com.example.bookmoth.domain.model.post.Profile;
import com.example.bookmoth.domain.usecase.post.FlaskUseCase;
import com.example.bookmoth.domain.usecase.post.PostUseCase;
import com.example.bookmoth.domain.usecase.profile.ProfileUseCase;
import com.example.bookmoth.ui.post.CommentActivity;
import com.example.bookmoth.ui.viewmodel.profile.ProfileViewModel;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private final Context context;
    private final List<Post> postList;
    private MediaPlayer mediaPlayer; // Giữ một MediaPlayer để tránh lỗi khi phát nhiều lần
    private long lastClickTime = 0;
    private PostUseCase postUseCase;
    private FlaskUseCase flaskUseCase;
    private static final long CLICK_DELAY = 500;
    private String profileId;
    public PostAdapter(Context context, List<Post> postList, PostUseCase postUseCase, FlaskUseCase flaskUseCase) {
        this.context = context;
        this.postList = postList;
        this.mediaPlayer = new MediaPlayer();
        this.postUseCase = postUseCase;
        this.flaskUseCase = flaskUseCase;

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
        getNameProfile(post.getAuthorId(), new NameCallback() {
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


        getImageProfile(1, new ImageCallback() {
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

        checkLikeStatus(post.getPostId(), holder);
        holder.btnLike.setOnClickListener(v -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastClickTime > CLICK_DELAY) {
                lastClickTime = currentTime;
                if (holder.isLiked) {
                    removeLike(post.getPostId(), holder);
                } else {
                    addLike(post.getPostId(), holder);
                }
            }
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

        if (mediaUrl != null && !mediaUrl.isEmpty()) {
            if (mediaUrl.endsWith(".jpg") || mediaUrl.endsWith(".png") || mediaUrl.endsWith(".jpeg")) {
                holder.imageView.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(mediaUrl)
                        .placeholder(R.drawable.placeholder_image) // Hiển thị ảnh mặc định khi tải
                        .error(R.drawable.error_image) // Hiển thị ảnh lỗi nếu tải thất bại
                        .into(holder.imageView);
            }
//            else if (mediaUrl.endsWith(".mp4") || mediaUrl.endsWith(".avi") || mediaUrl.endsWith(".mkv")) {
//                holder.videoView.setVisibility(View.VISIBLE);
//                holder.videoView.setVideoURI(Uri.parse(mediaUrl));
//                holder.videoView.setOnPreparedListener(mp -> {
//                    mp.setLooping(true);
//                    mp.start();
//                });
//            } else if (mediaUrl.endsWith(".mp3") || mediaUrl.endsWith(".wav")) {
//                holder.btnPlayAudio.setVisibility(View.VISIBLE);
//                holder.btnPlayAudio.setOnClickListener(v -> playAudio(mediaUrl));
//            }
        }
    }
    private void getImageProfile(int authorId, ImageCallback callback) {
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


    private void addLike(int postId, PostViewHolder holder) {
        getCurrentLikeCount(postId, new LikeCountCallback() {
            @Override
            public void onLikeCountReceived(int currentLikeCount) {
                // Tăng số lượng like
                int newLikeCount = currentLikeCount + 1;
                holder.isLiked = true;
                holder.btnLike.setImageResource(R.drawable.button_liked);

                // Cập nhật UI tạm thời
                holder.countLike.setText(String.valueOf(newLikeCount));

                // Chuẩn bị dữ liệu gửi API
                Map<String, Object> body = new HashMap<>();
                body.put("post_id", postId);
                profileId = SecureStorage.getToken("profileId");
                getProfile();
                body.put("user_id", profileId); // Thay bằng getCurrentUserId() nếu có

                // Gửi request đến API để thêm like
                postUseCase.addLike(body).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (!response.isSuccessful()) {
                            Log.e("Like", "Lỗi thêm like: " + response.errorBody());
                            rollbackLikeUI(holder, currentLikeCount, false);
                        } else {
                            updateLikeCount(postId, newLikeCount);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("Like", "Lỗi kết nối khi thêm like", t);
                        rollbackLikeUI(holder, currentLikeCount, false);
                    }
                });
            }
        });
    }


    private void removeLike(int postId, PostViewHolder holder) {
        profileId = SecureStorage.getToken("profileId");
        getProfile();
        String userId = profileId; // Cần lấy userId thực tế từ session hoặc database
        String url = "https://vhqcdiaoqrlcsnqvjpqh.supabase.co/rest/v1/likes?" + "post_id=eq." + postId + "&user_id=eq." + userId;

        // Cập nhật UI ngay lập tức
        holder.isLiked = false;
        String likeText = holder.countLike.getText().toString().trim();
        int currentLikeCount = likeText.isEmpty() ? 0 : Integer.parseInt(likeText);
        int newLikeCount = currentLikeCount - 1;
        holder.btnLike.setImageResource(R.drawable.button_like);
        // Gửi request đến API
        postUseCase.removeLike(url).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    Log.e("Like", "Lỗi xóa like: " + response.errorBody());
                    rollbackLikeUI(holder, currentLikeCount, true);
                }
                else {
                    updateLikeCount(postId, newLikeCount);
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
    // Hàm rollback UI khi request thất bại
    private void rollbackLikeUI(PostViewHolder holder, int likeCount, boolean isLiked) {
        holder.isLiked = isLiked;
        holder.countLike.setText(String.valueOf(likeCount));
        holder.btnLike.setImageResource(isLiked ? R.drawable.button_liked : R.drawable.button_like);
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

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvTimestamp,countLike, countComment, tabWorks, nameProfile;
        ImageView imageView;
        ImageButton btnLike, btnComment, btnProfile;
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
