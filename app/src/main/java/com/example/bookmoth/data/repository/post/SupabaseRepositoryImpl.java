package com.example.bookmoth.data.repository.post;

import com.example.bookmoth.data.remote.post.SupabaseApiService;
import com.example.bookmoth.data.remote.utils.RetrofitClient;
import com.example.bookmoth.domain.model.post.Comment;
import com.example.bookmoth.domain.model.post.Post;
import com.example.bookmoth.domain.repository.post.SupabaseRepository;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class SupabaseRepositoryImpl implements SupabaseRepository {
    private SupabaseApiService supabaseApiService;
    public SupabaseRepositoryImpl(){
        supabaseApiService = RetrofitClient.getServerPost().create(SupabaseApiService.class);
    }

    @Override
    public Call<List<Post>> getPosts() {
        return supabaseApiService.getPosts();
    }

    @Override
    public Call<List<Post>> getPostById(String postID) {
        return supabaseApiService.getPostById(postID);
    }

    @Override
    public Call<List<Post>> getPostByIdUser(String author_id, String status) {
        return supabaseApiService.getPostByIdUser(author_id,status);
    }

    @Override
    public Call<ResponseBody> updateLike(String postId, Map<String, Object> updateData) {
        return supabaseApiService.updateLike(postId,updateData);
    }

    @Override
    public Call<Void> createPost(Map<String, Object> post) {
        return supabaseApiService.createPost(post);
    }

    @Override
    public Call<Void> updatePost(String id, Map<String, Object> post) {
        return supabaseApiService.updatePost(id,post);
    }

    @Override
    public Call<ResponseBody> updatePostStatus(String postId, Map<String, Object> body) {
        return supabaseApiService.updatePostStatus(postId,body);
    }
    @Override
    public Call<ResponseBody> updateComment(String postId, Map<String, Object> updateData) {
        return supabaseApiService.updateComment(postId,updateData);
    }

    @Override
    public Call<List<Map<String, Object>>> checkLikeStatus(String postId, String userId, String select) {
        return supabaseApiService.checkLikeStatus(postId,userId,select);
    }

    @Override
    public Call<ResponseBody> addLike(Map<String, Object> body) {
        return supabaseApiService.addLike(body);
    }

    @Override
    public Call<ResponseBody> removeLike(String url) {
        return supabaseApiService.removeLike(url);
    }

    @Override
    public Call<Integer> getLikeForId(String postID) {
        return supabaseApiService.getLikeForId(postID);
    }

    @Override
    public Call<List<Comment>> getComments() {
        return supabaseApiService.getComments();
    }

    @Override
    public Call<List<Comment>> getCommentForId(String postID) {
        return supabaseApiService.getCommentForId(postID);
    }

    @Override
    public Call<Void> addComment(Map<String, Object> comment) {
        return supabaseApiService.addComment(comment);
    }

    @Override
    public Call<ResponseBody> updateLikeComment(String comnentId, Map<String, Object> updateData) {
        return supabaseApiService.updateLikeComment(comnentId,updateData);
    }

    @Override
    public Call<List<Map<String, Object>>> checkLikeComment(String postId, String userId, String select) {
        return supabaseApiService.checkLikeComment(postId,userId,select);
    }

    @Override
    public Call<ResponseBody> addLikeComment(Map<String, Object> body) {
        return supabaseApiService.addLikeComment(body);
    }

    @Override
    public Call<ResponseBody> removeLikeComment(String url) {
        return supabaseApiService.removeLikeComment(url);
    }

    @Override
    public Call<ResponseBody> uploadFile(String url, RequestBody fileBody) {
        return supabaseApiService.uploadFile(url,fileBody);
    }
}
