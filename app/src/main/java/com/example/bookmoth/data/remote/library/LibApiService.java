package com.example.bookmoth.data.remote.library;

import com.example.bookmoth.domain.model.library.Chapter;
import com.example.bookmoth.domain.model.library.Work;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface LibApiService {
    //CDN Call
    @Streaming
    @GET("/libapi/cdn/read/{content}")
    Call<ResponseBody> getChapterContent(@Path("content") String content_url);

    @GET("/libapi/cdn/cover/{cover}")
    Call<ResponseBody> getWorkCover(@Path("cover") String cover_url);

    //Work-related list call
    @GET("/libapi/owned")
    Call<List<Work>> getOwnedWorks();

    @GET("/libapi/created")
    Call<List<Work>> getCreatedWorks();

    @GET("/libapi/works")
    Call<List<Work>> getWorks(@QueryMap Map<String, String> query);

    @GET("/libapi/work/{wid}/chapters")
    Call<List<Chapter>> getChaptersOfWork(@Path("wid") int work_id, @QueryMap Map<String, String> query);

    //Individual object call
    @GET("/libapi/work/{wid}")
    Call<Work> getWorkById(@Path("wid") int work_id);

    @GET("/libapi/chapter/{cid}")
    Call<Chapter> getChapterById(@Path("cid") int chapter_id);

    @Multipart
    @POST("/libapi/work/post")
    Call<ResponseBody> postWork(
            @Part MultipartBody.Part cover,
            @Part("json") RequestBody info);

    @Multipart
    @POST("/libapi/work/{wid}/chapter/post")
    Call<ResponseBody> postChapter(
        @Path("wid") int work_id,
        @Part MultipartBody.Part content,
        @Part("json") RequestBody info
    );

    @GET("/libapi/work/{wid}/stats")
    Call<ResponseBody> getWorkStats(
        @Path("wid") int work_id
    );

    @DELETE("/libapi/work/{wid}/delete")
    Call<ResponseBody> deleteWork(
        @Path("wid") int work_id
    );

    @Multipart
    @PUT("/libapi/work/{wid}/put")
    Call<ResponseBody> putWork(
            @Path("wid") int work_id,
            @Part MultipartBody.Part cover,
            @Part("json") RequestBody info);

    @DELETE("/libapi/chapter/{cid}/delete")
    Call<ResponseBody> deleteChapter(
            @Path("cid") int chapter_id
    );

    @Multipart
    @PUT("/libapi/chapter/{cid}/put")
    Call<ResponseBody> putChapter(
            @Path("cid") int chapter_id,
            @Part MultipartBody.Part content,
            @Part("json") RequestBody info);
}
