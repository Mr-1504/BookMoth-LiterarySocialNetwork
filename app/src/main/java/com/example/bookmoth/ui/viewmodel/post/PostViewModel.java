package com.example.bookmoth.ui.viewmodel.post;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.bookmoth.R;
import com.example.bookmoth.domain.model.post.Comment;
import com.example.bookmoth.domain.model.post.Post;
import com.example.bookmoth.domain.usecase.post.PostUseCase;
import com.example.bookmoth.ui.post.CommentActivity;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostViewModel {
    private PostUseCase postUseCase;

    public PostViewModel(PostUseCase postUseCase) {
        this.postUseCase = postUseCase;
    }

    public void getPosts(Context context, final OnGetPost listener) {
        {
            postUseCase.getPosts().enqueue(new Callback<List<Post>>() {
                @Override
                public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Post> posts = response.body();
                        Log.d("Supabase", "Số lượng bài viết: " + posts.size());
                        listener.onGetPostSuccess(posts);

                    } else {
                        Log.e("Supabase", context.getString(R.string.error_get_post)
                                + response.errorBody());
                        listener.onGetPostFailure(context.getString(R.string.error_get_post)
                                + response.errorBody().toString());
                    }
                }

                @Override
                public void onFailure(Call<List<Post>> call, Throwable t) {
                    listener.onGetPostFailure(context.getString(R.string.error_connecting_to_server)
                            + t.getMessage());
                }
            });
        }
    }

    public void getPostById(String postId, final OnGetPost listener) {
        postUseCase.getPostById(postId).enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Post> posts = response.body();
                    Log.d("Supabase", "Số lượng bài viết: " + posts.size());
                    listener.onGetPostSuccess(posts);

                } else {
                    Log.e("Supabase", "Lỗi lấy bài viết: " + response.errorBody());
                    listener.onGetPostFailure(
                            "Lỗi lấy bài viết: " + response.errorBody().toString()
                    );
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                listener.onGetPostFailure("Lỗi kết nối API: " + t.getMessage());
            }
        });
    }

    public void getPostByIdUser(String author_id, final OnGetPost listener) {
        postUseCase.getPostByIdUser(author_id,"eq.0").enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<Post> posts = response.body();
                    Log.d("Supabase", "Số lượng bài viết: " + posts.size());
                    listener.onGetPostSuccess(posts);

                } else {
                    Log.e("Supabase", "Lỗi lấy bài đăng: ");
                    listener.onGetPostFailure(
                            "Lỗi lấy bài đăng"
                    );
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                listener.onGetPostFailure("Lỗi kết nối API: " + t.getMessage());
            }
        });
    }



    public void createPost(Map<String, Object> post, final OnSupbaBaseListener listener) {
        postUseCase.createPost(post).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có phản hồi";
                        Log.e("SAVE_POST_ERROR", "Response: " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    listener.onUnSuccess("Lỗi tạo bài viết: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                listener.onFailure("Lỗi kết nối API: " + t.getMessage());
            }
        });
    }

    public void updatePost(String id, Map<String, Object> post, final OnSupbaBaseListener listener){
        postUseCase.updatePost(id, post).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    listener.onSuccess();
                }
                else {
                    listener.onUnSuccess("Lỗi update bài viết"+ response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                listener.onFailure("Lỗi kết nối API: " + t.getMessage());
            }
        });
    }

    public void addComment(Map<String, Object> comment, final OnSupbaBaseListener listener){
        postUseCase.addComment(comment).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Lỗi không xác định";
                        Log.e("Supabase", "Lỗi khi thêm bình luận: " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    listener.onUnSuccess("Lỗi tạo bài viết: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                listener.onFailure("Lỗi kết nối API: " + t.getMessage());
            }
        });
    }

    public void getCommentForId(String postID, final OnGetComment listener){
        postUseCase.getCommentForId(postID).enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if(response.isSuccessful()){
                    List<Comment> comments = response.body();
                    listener.onGetCommentSuccess(comments);
                }
                else {
                    listener.onGetCommentUnSuccess("Lỗi lấy comment"+ response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                listener.onGetCommentFailure("Lỗi kết nối API: " + t.getMessage());
            }
        });
    }
    public void updateComment(String postId, Map<String, Object> updateData, final OnSupbaBaseListener listener){
        postUseCase.updateComment(postId, updateData).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    listener.onSuccess();
                }
                else {
                    listener.onUnSuccess("Lỗi update comment"+ response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                listener.onFailure("Lỗi kết nối API: " + t.getMessage());
            }
        });
    }
    public void uploadFile(String uploadUrl, RequestBody requestBody, final OnStorageListener listener){
        postUseCase.uploadFile(uploadUrl, requestBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    listener.onStorageSuccess("Upload file thành công");
                }
                else {
                    listener.onStorageFailure("Lỗi upload file"+ response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                listener.onStorageFailure("Lỗi kết nối API: " + t.getMessage());
            }
        });
    }

    public interface OnGetPost {
        public void onGetPostSuccess(List<Post> posts);

        public void onGetPostFailure(String message);
    }

    public interface OnStorageListener {
        public void onStorageSuccess(String message);

        public void onStorageUnSuccess(String message);

        public void onStorageFailure(String message);
    }
    public interface OnSupbaBaseListener {
        public void onSuccess();
        public void onUnSuccess(String message);

        public void onFailure(String message);
    }
    public interface OnGetComment {
        public void onGetCommentSuccess(List<Comment> comments);
        public void onGetCommentUnSuccess(String message);

        public void onGetCommentFailure(String message);
    }
}
