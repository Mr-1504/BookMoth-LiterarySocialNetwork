package com.example.bookmoth.data.repository.shop;

import com.example.bookmoth.data.remote.shop.ShopApiService;
import com.example.bookmoth.data.remote.utils.RetrofitClient;
import com.example.bookmoth.domain.model.shop.Category;
import com.example.bookmoth.domain.model.shop.Chapter;
import com.example.bookmoth.domain.model.shop.Profile;
import com.example.bookmoth.domain.model.shop.Work;
import com.example.bookmoth.domain.model.shop.WorkResponse;
import com.example.bookmoth.domain.repository.shop.ShopRepository;

import java.util.List;

import retrofit2.Call;

public class ShopRepositoryImpl implements ShopRepository {
    private final ShopApiService shopApiService;

    public ShopRepositoryImpl() {
        this.shopApiService = RetrofitClient.getShopServerRetrofit().create(ShopApiService.class);
    }

    @Override
    public Call<List<Category>> getCategories() {
        return shopApiService.getCategories();
    }

    @Override
    public Call<List<Work>> getWorks() {
        return shopApiService.getWorks();
    }

    @Override
    public Call<List<Work>> getNewReleases() {
        return shopApiService.getNewReleases();
    }

    @Override
    public Call<List<Work>> getPopular() {
        return shopApiService.getPopular();
    }

    @Override
    public Call<Work> getWorkById(int workId) {
        return shopApiService.getWorkById(workId);
    }

    @Override
    public Call<List<Chapter>> getChaptersByWorkId(int workId) {
        return shopApiService.getChaptersByWorkId(workId);
    }

    @Override
    public Call<List<Category>> getCategoriesByWorkId(int workId) {
        return shopApiService.getCategoriesByWorkId(workId);
    }

    @Override
    public Call<Profile> getProfileById(int profileId) {
        return shopApiService.getProfileById(profileId);
    }

    @Override
    public Call<WorkResponse> getWorksByTag(String tag, int page, int perPage) {
        return shopApiService.getWorksByTag(tag, page, perPage);
    }
}
