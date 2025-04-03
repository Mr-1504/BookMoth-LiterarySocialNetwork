package com.example.bookmoth.data.remote.shop;

import com.example.bookmoth.domain.model.shop.Category;
import com.example.bookmoth.domain.model.shop.Chapter;
import com.example.bookmoth.domain.model.shop.Profile;
import com.example.bookmoth.domain.model.shop.Work;
import com.example.bookmoth.domain.model.shop.WorkResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ShopApiService {
    @GET("categories")
    Call<List<Category>> getCategories();

    @GET("/works")
    Call<List<Work>> getWorks();

    @GET("/new_releases")
    Call<List<Work>> getNewReleases();

    @GET("/popular")
    Call<List<Work>> getPopular();

    @GET("/works/{work_id}")
    Call<Work> getWorkById(@Path("work_id") int workId);

    @GET("/chapters/{work_id}")
    Call<List<Chapter>> getChaptersByWorkId(@Path("work_id") int workId);

    @GET("/categories/{work_id}")
    Call<List<Category>> getCategoriesByWorkId(@Path("work_id") int workId);

    @GET("profiles/{work_id}")
    Call<Profile> getProfileById(@Path("work_id") int profileId);

    @GET("works/tags/{tag}")
    Call<WorkResponse> getWorksByTag (
            @Path("tag") String tag,
            @Query("page") int page,
            @Query("per_page") int perPage
    );
}
