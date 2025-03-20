package com.example.bookmoth.domain.usecase.post;

import com.example.bookmoth.domain.model.post.Comment;
import com.example.bookmoth.domain.model.post.Post;
import com.example.bookmoth.domain.repository.post.SupabaseRepository;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class PostUseCase {
    private SupabaseRepository supabaseRepository;

    public PostUseCase(SupabaseRepository supabaseRepository) {
        this.supabaseRepository = supabaseRepository;
    }

    public Call<List<Post>> getPosts() {
        return supabaseRepository.getPosts();
    }


    public Call<List<Post>> getPostById(String postID) {
        return supabaseRepository.getPostById(postID);
    }


    public Call<List<Post>> getPostByIdUser(String author_id) {
        return supabaseRepository.getPostByIdUser(author_id);
    }


    public Call<ResponseBody> updateLike(
            String postId,  // Dùng @Query thay vì @Path
            Map<String, Object> updateData
    ) {
        return supabaseRepository.updateLike(postId, updateData);
    }


    public Call<Void> createPost(Map<String, Object> post) {
        return supabaseRepository.createPost(post);
    }


    public Call<ResponseBody> updateComment(
            String postId,  // Dùng @Query thay vì @Path
            Map<String, Object> updateData
    ) {
        return supabaseRepository.updateComment(postId, updateData);
    }


    // Like
    // Kiểm tra trạng thái like
    public Call<List<Map<String, Object>>> checkLikeStatus(
            String postId,
            String userId,
            String select
    ) {
        return supabaseRepository.checkLikeStatus(postId, userId, select);
    }


    // Thêm like (POST vào bảng "likes")
    public Call<ResponseBody> addLike(Map<String, Object> body) {
        return supabaseRepository.addLike(body);
    }

    // Xóa like (DELETE từ bảng "likes" với điều kiện)
    public Call<ResponseBody> removeLike(String url) {
        return supabaseRepository.removeLike(url);
    }


    public Call<Integer> getLikeForId(String postID) {
        return supabaseRepository.getLikeForId(postID);
    }


    public Call<List<Comment>> getComments() {
        return supabaseRepository.getComments();
    }


    public Call<List<Comment>> getCommentForId(String postID) {
        return supabaseRepository.getCommentForId(postID);
    }


    public Call<Void> addComment(Map<String, Object> comment) {
        return supabaseRepository.addComment(comment);
    }


    public Call<ResponseBody> updateLikeComment(
            String comnentId,  // Dùng @Query thay vì @Path
            Map<String, Object> updateData
    ) {
        return supabaseRepository.updateLikeComment(comnentId, updateData);
    }


    public Call<List<Map<String, Object>>> checkLikeComment(
            String postId,
            String userId,
            String select
    ) {
        return supabaseRepository.checkLikeComment(postId, userId, select);
    }


    // Thêm like (POST vào bảng "likes")
    public Call<ResponseBody> addLikeComment(Map<String, Object> body) {
        return supabaseRepository.addLikeComment(body);
    }

    // Xóa like (DELETE từ bảng "likes" với điều kiện)
    public Call<ResponseBody> removeLikeComment(String url) {
        return supabaseRepository.removeLikeComment(url);
    }

    // upload file
    public Call<ResponseBody> uploadFile(
            String url,
            RequestBody fileBody
    ) {
        return supabaseRepository.uploadFile(url, fileBody);
    }
}
