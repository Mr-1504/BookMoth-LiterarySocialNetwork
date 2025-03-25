package com.example.bookmoth.domain.repository.post;

import com.example.bookmoth.domain.model.post.Comment;
import com.example.bookmoth.domain.model.post.Post;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface SupabaseRepository {


    Call<List<Post>> getPosts();


    Call<List<Post>> getPostById(String postID);


    Call<List<Post>> getPostByIdUser(String author_id, String status);


    Call<ResponseBody> updateLike(
            String postId,  // Dùng @Query thay vì @Path
            Map<String, Object> updateData
    );


    Call<Void> createPost(Map<String, Object> post);

    Call<Void> updatePost(String id, Map<String, Object> post);

    Call<ResponseBody> updatePostStatus(String postId, Map<String, Object> body);

    Call<ResponseBody> updateComment(
            String postId,  // Dùng @Query thay vì @Path
            Map<String, Object> updateData
    );


    // Like
    // Kiểm tra trạng thái like

    Call<List<Map<String, Object>>> checkLikeStatus(
            String postId,
            String userId,
            String select
    );


    // Thêm like (POST vào bảng "likes")

    Call<ResponseBody> addLike(Map<String, Object> body);

    // Xóa like (DELETE từ bảng "likes" với điều kiện)

    Call<ResponseBody> removeLike(String url);


    Call<Integer> getLikeForId(String postID);


    Call<List<Comment>> getComments();


    Call<List<Comment>> getCommentForId(String postID);


    Call<Void> addComment(Map<String, Object> comment);

    Call<ResponseBody> removeComment(String url);

    Call<ResponseBody> updateLikeComment(
            String comnentId,  // Dùng @Query thay vì @Path
            Map<String, Object> updateData
    );


    Call<List<Map<String, Object>>> checkLikeComment(
            String postId,
            String userId,
            String select
    );


    // Thêm like (POST vào bảng "likes")

    Call<ResponseBody> addLikeComment(Map<String, Object> body);

    // Xóa like (DELETE từ bảng "likes" với điều kiện)

    Call<ResponseBody> removeLikeComment(String url);

    // upload file

    Call<ResponseBody> uploadFile(
            String url,
            RequestBody fileBody
    );
}
