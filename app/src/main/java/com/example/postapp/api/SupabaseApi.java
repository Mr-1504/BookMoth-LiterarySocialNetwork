
package com.example.postapp.api;

import com.example.postapp.models.Comment;
import com.example.postapp.models.Post;

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
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface SupabaseApi {
    String DATABASE_URL = "https://vhqcdiaoqrlcsnqvjpqh.supabase.co/rest/v1/";
    String STORAGE_URL = "https://vhqcdiaoqrlcsnqvjpqh.supabase.co/storage/v1/";
    String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InZocWNkaWFvcXJsY3NucXZqcHFoIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mzg2NTY4NjcsImV4cCI6MjA1NDIzMjg2N30.uZ1zHyL4LMAsy_BcNE8MbJz73jw_9Mhj7dHtVKds6Qw";

    @Headers({
            "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InZocWNkaWFvcXJsY3NucXZqcHFoIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mzg2NTY4NjcsImV4cCI6MjA1NDIzMjg2N30.uZ1zHyL4LMAsy_BcNE8MbJz73jw_9Mhj7dHtVKds6Qw",
            "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InZocWNkaWFvcXJsY3NucXZqcHFoIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mzg2NTY4NjcsImV4cCI6MjA1NDIzMjg2N30.uZ1zHyL4LMAsy_BcNE8MbJz73jw_9Mhj7dHtVKds6Qw",
            "Content-Type: application/json"
    })

    @GET("posts")
    Call<List<Post>> getPosts();

    @GET("posts")
    @Headers({
            "apikey: " + SupabaseApi.SUPABASE_KEY,
            "Authorization: Bearer " + SupabaseApi.SUPABASE_KEY,
            "Content-Type: application/json"
    })
    Call<List<Post>> getPostById(@Query("post_id") String postID);

    @PATCH("posts")
    @Headers({
            "apikey: " + SupabaseApi.SUPABASE_KEY,
            "Authorization: Bearer " + SupabaseApi.SUPABASE_KEY,
            "Content-Type: application/json"
    })
    Call<ResponseBody> updateLike(
            @Query("post_id") String postId,  // Dùng @Query thay vì @Path
            @Body Map<String, Object> updateData
    );


    @POST("posts")
    @Headers({
            "apikey: " + SupabaseApi.SUPABASE_KEY,
            "Authorization: Bearer " + SupabaseApi.SUPABASE_KEY,
            "Content-Type: application/json"
    })
    Call<Void> createPost(@Body Map<String, Object> post);

    @PATCH("posts")

    @Headers({
            "apikey: " + SupabaseApi.SUPABASE_KEY,
            "Authorization: Bearer " + SupabaseApi.SUPABASE_KEY,
            "Content-Type: application/json"
    })
    Call<ResponseBody> updateComment(
            @Query("post_id") String postId,  // Dùng @Query thay vì @Path
            @Body Map<String, Object> updateData
    );


    // Like
    // Kiểm tra trạng thái like
    @GET("likes")
    @Headers({
            "apikey: " + SupabaseApi.SUPABASE_KEY,
            "Authorization: Bearer " + SupabaseApi.SUPABASE_KEY,
            "Content-Type: application/json"
    })
    Call<List<Map<String, Object>>> checkLikeStatus(
            @Query("post_id") String postId,
            @Query("user_id") String userId,
            @Query("select") String select
    );


    // Thêm like (POST vào bảng "likes")
    @POST("likes")
    @Headers({
            "apikey: " + SupabaseApi.SUPABASE_KEY,
            "Authorization: Bearer " + SupabaseApi.SUPABASE_KEY,
            "Content-Type: application/json"
    })
    Call<ResponseBody> addLike(@Body Map<String, Object> body);

    // Xóa like (DELETE từ bảng "likes" với điều kiện)
    @DELETE
    @Headers({
            "apikey: " + SupabaseApi.SUPABASE_KEY,
            "Authorization: Bearer " + SupabaseApi.SUPABASE_KEY,
            "Content-Type: application/json",
            "Prefer: return=minimal"
    })
    Call<ResponseBody> removeLike(@Url String url);

    @GET("likes")
    @Headers({
            "apikey: " + SupabaseApi.SUPABASE_KEY,
            "Authorization: Bearer " + SupabaseApi.SUPABASE_KEY,
            "Content-Type: application/json"
    })
    Call<Integer> getLikeForId(@Query("post_id") String postID);



    @GET("comments")
    @Headers({
            "apikey: " + SupabaseApi.SUPABASE_KEY,
            "Authorization: Bearer " + SupabaseApi.SUPABASE_KEY,
            "Content-Type: application/json"
    })
    Call<List<Comment>> getComments();
    @GET("comments")
    @Headers({
            "apikey: " + SupabaseApi.SUPABASE_KEY,
            "Authorization: Bearer " + SupabaseApi.SUPABASE_KEY,
            "Content-Type: application/json"
    })
    Call<List<Comment>> getCommentForId(@Query("post_id") String postID);
    @POST("comments")
    @Headers({
            "apikey: " + SupabaseApi.SUPABASE_KEY,
            "Authorization: Bearer " + SupabaseApi.SUPABASE_KEY,
            "Content-Type: application/json"
    })
    Call<Void> addComment(@Body Map<String, Object> comment);

    @PATCH("comments")
    @Headers({
            "apikey: " + SupabaseApi.SUPABASE_KEY,
            "Authorization: Bearer " + SupabaseApi.SUPABASE_KEY,
            "Content-Type: application/json"
    })
    Call<ResponseBody> updateLikeComment(
            @Query("id_comment") String comnentId,  // Dùng @Query thay vì @Path
            @Body Map<String, Object> updateData
    );




    @GET("likecomment")
    @Headers({
            "apikey: " + SupabaseApi.SUPABASE_KEY,
            "Authorization: Bearer " + SupabaseApi.SUPABASE_KEY,
            "Content-Type: application/json"
    })
    Call<List<Map<String, Object>>> checkLikeComment(
            @Query("id_comment") String postId,
            @Query("id_user") String userId,
            @Query("select") String select
    );


    // Thêm like (POST vào bảng "likes")
    @POST("likecomment")
    @Headers({
            "apikey: " + SupabaseApi.SUPABASE_KEY,
            "Authorization: Bearer " + SupabaseApi.SUPABASE_KEY,
            "Content-Type: application/json"
    })
    Call<ResponseBody> addLikeComment(@Body Map<String, Object> body);

    // Xóa like (DELETE từ bảng "likes" với điều kiện)
    @DELETE
    @Headers({
            "apikey: " + SupabaseApi.SUPABASE_KEY,
            "Authorization: Bearer " + SupabaseApi.SUPABASE_KEY,
            "Content-Type: application/json",
            "Prefer: return=minimal"
    })
    Call<ResponseBody> removeLikeComment(@Url String url);
    // upload file
    @PUT
    Call<ResponseBody> uploadFile(
            @Url String url,
            @HeaderMap Map<String, String> headers,
            @Body RequestBody fileBody
    );
}
