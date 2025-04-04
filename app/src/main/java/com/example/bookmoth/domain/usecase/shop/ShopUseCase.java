package com.example.bookmoth.domain.usecase.shop;

import com.example.bookmoth.domain.model.shop.Category;
import com.example.bookmoth.domain.model.shop.Chapter;
import com.example.bookmoth.domain.model.shop.Profile;
import com.example.bookmoth.domain.model.shop.Work;
import com.example.bookmoth.domain.model.shop.WorkResponse;
import com.example.bookmoth.domain.repository.shop.ShopRepository;

import java.util.List;

import retrofit2.Call;

public class ShopUseCase {
    private ShopRepository shopRepository;

    public ShopUseCase(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    public Call<List<Category>> getCategories() {
        return shopRepository.getCategories();
    }

    public Call<List<Work>> getWorks() {
        return shopRepository.getWorks();
    }

    public Call<List<Work>> getNewReleases() {
        return shopRepository.getNewReleases();
    }

    public Call<List<Work>> getPopular() {
        return shopRepository.getPopular();
    }

    public Call<Work> getWorkById(int workId) {
        return shopRepository.getWorkById(workId);
    }

    public Call<List<Chapter>> getChaptersByWorkId(int workId) {
        return shopRepository.getChaptersByWorkId(workId);
    }

    public Call<List<Category>> getCategoriesByWorkId(int workId) {
        return shopRepository.getCategoriesByWorkId(workId);
    }

    public Call<Profile> getProfileById(int profileId) {
        return shopRepository.getProfileById(profileId);
    }

    public Call<WorkResponse> getWorksByTag(String tag, int page, int perPage) {
        return shopRepository.getWorksByTag(tag, page, perPage);
    }
}
