package com.example.bookmoth.domain.repository.shop;

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

public interface ShopRepository {

    Call<List<Category>> getCategories();

    Call<List<Work>> getWorks();

    Call<List<Work>> getNewReleases();

    Call<List<Work>> getPopular();

    Call<Work> getWorkById( int workId);

    Call<List<Chapter>> getChaptersByWorkId(int workId);

    Call<List<Category>> getCategoriesByWorkId(int workId);

    Call<Profile> getProfileById( int profileId);

    Call<WorkResponse> getWorksByTag (String tag, int page, int perPage);

    Call<List<Work>> getWorkByTitle(String title);
}
