package com.example.bookmoth.data.remote.post;

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
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface SupabaseApiService {


    @GET("rest/v1/posts")
    Call<List<Post>> getPosts();

    @GET("rest/v1/posts")
    Call<List<Post>> getPostById(@Query("post_id") String postID);

    @GET("rest/v1/posts")
    Call<List<Post>> getPostByIdUser(@Query("author_id") String author_id);

    @PATCH("rest/v1/posts")
    Call<ResponseBody> updateLike(
            @Query("post_id") String postId,  // Dùng @Query thay vì @Path
            @Body Map<String, Object> updateData
    );


    @POST("rest/v1/posts")
    Call<Void> createPost(@Body Map<String, Object> post);

    @PATCH("rest/v1/posts")
    Call<ResponseBody> updateComment(
            @Query("post_id") String postId,  // Dùng @Query thay vì @Path
            @Body Map<String, Object> updateData
    );


    // Like
    // Kiểm tra trạng thái like
    @GET("rest/v1/likes")
    Call<List<Map<String, Object>>> checkLikeStatus(
            @Query("post_id") String postId,
            @Query("user_id") String userId,
            @Query("select") String select
    );


    // Thêm like (POST vào bảng "likes")
    @POST("rest/v1/likes")
    Call<ResponseBody> addLike(@Body Map<String, Object> body);

    // Xóa like (DELETE từ bảng "likes" với điều kiện)
    @DELETE()
    Call<ResponseBody> removeLike(@Url String url);

    @GET("rest/v1/likes")
    Call<Integer> getLikeForId(@Query("post_id") String postID);


    @GET("rest/v1/comments")
    Call<List<Comment>> getComments();

    @GET("rest/v1/comments")
    Call<List<Comment>> getCommentForId(@Query("post_id") String postID);

    @POST("rest/v1/comments")
    Call<Void> addComment(@Body Map<String, Object> comment);

    @PATCH("rest/v1/comments")
    Call<ResponseBody> updateLikeComment(
            @Query("id_comment") String comnentId,  // Dùng @Query thay vì @Path
            @Body Map<String, Object> updateData
    );


    @GET("rest/v1/likecomment")
    Call<List<Map<String, Object>>> checkLikeComment(
            @Query("id_comment") String postId,
            @Query("id_user") String userId,
            @Query("select") String select
    );


    // Thêm like (POST vào bảng "likes")
    @POST("rest/v1/likecomment")
    Call<ResponseBody> addLikeComment(@Body Map<String, Object> body);

    // Xóa like (DELETE từ bảng "likes" với điều kiện)
    @DELETE()
    Call<ResponseBody> removeLikeComment(@Url String url);

    // upload file
    @PUT()
    Call<ResponseBody> uploadFile(
            @Url String url,
            @Body RequestBody fileBody
    );
}
