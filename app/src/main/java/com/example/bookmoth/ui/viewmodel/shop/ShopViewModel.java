package com.example.bookmoth.ui.viewmodel.shop;

import android.content.Context;

import com.example.bookmoth.R;
import com.example.bookmoth.domain.model.shop.Category;
import com.example.bookmoth.domain.model.shop.Chapter;
import com.example.bookmoth.domain.model.shop.Profile;
import com.example.bookmoth.domain.model.shop.Work;
import com.example.bookmoth.domain.model.shop.WorkResponse;
import com.example.bookmoth.domain.usecase.shop.ShopUseCase;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopViewModel {
    private ShopUseCase shopUseCase;

    public ShopViewModel(ShopUseCase shopUseCase) {
        this.shopUseCase = shopUseCase;
    }

    public void getCategories(Context context, final OnCategoryClickListener listener){
        shopUseCase.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body();
                    listener.onSuccess(categories); // Example: click on the first category
                } else {
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                listener.onFailure(context.getString(R.string.error_connecting_to_server));
            }
        });
    }

    public void getNewReleases(Context context, final OnGetNewReleasesListener listener){
        shopUseCase.getNewReleases().enqueue(new Callback<List<Work>>() {
            @Override
            public void onResponse(Call<List<Work>> call, Response<List<Work>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Work> works = response.body();
                    listener.onSuccess(works);
                } else {
                    listener.onFailure(context.getString(R.string.undefined_error));
                }
            }

            @Override
            public void onFailure(Call<List<Work>> call, Throwable t) {
                listener.onFailure(context.getString(R.string.error_connecting_to_server));
            }
        });
    }

    public void getPopular(Context context, final OnGetPopularListener listener){
        shopUseCase.getPopular().enqueue(new Callback<List<Work>>() {
            @Override
            public void onResponse(Call<List<Work>> call, Response<List<Work>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Work> works = response.body();
                    listener.onSuccess(works);
                } else {
                    listener.onFailure(context.getString(R.string.undefined_error));
                }
            }

            @Override
            public void onFailure(Call<List<Work>> call, Throwable t) {
                listener.onFailure(context.getString(R.string.error_connecting_to_server));
            }
        });
    }

    public void getWorksByTag(Context context, String tag, int page, int perPage, final OnGetWorksByTagListener listener) {
        shopUseCase.getWorksByTag(tag, page, perPage).enqueue(new Callback<WorkResponse>() {
            @Override
            public void onResponse(Call<WorkResponse> call, Response<WorkResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WorkResponse workResponse = response.body();
                    listener.onSuccess(workResponse);
                } else {
                    listener.onFailure(context.getString(R.string.undefined_error));
                }
            }

            @Override
            public void onFailure(Call<WorkResponse> call, Throwable t) {
                listener.onFailure(context.getString(R.string.error_connecting_to_server));
            }
        });
    }

    public void getWorkById(Context context, int workId, final OnGetWorkByIdListener listener) {
        shopUseCase.getWorkById(workId).enqueue(new Callback<Work>() {
            @Override
            public void onResponse(Call<Work> call, Response<Work> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Work work = response.body();
                    listener.onSuccess(work);
                } else {
                    listener.onFailure(context.getString(R.string.undefined_error));
                }
            }

            @Override
            public void onFailure(Call<Work> call, Throwable t) {
                listener.onFailure(context.getString(R.string.error_connecting_to_server));
            }
        });
    }

    public void getChaptersByWorkId(Context context, int workId, final OnGetChaptersByWorkIdListener listener) {
        shopUseCase.getChaptersByWorkId(workId).enqueue(new Callback<List<Chapter>>() {
            @Override
            public void onResponse(Call<List<Chapter>> call, Response<List<Chapter>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Chapter> chapters = response.body();
                    listener.onSuccess(chapters);
                } else {
                    listener.onError(context.getString(R.string.undefined_error));
                }
            }

            @Override
            public void onFailure(Call<List<Chapter>> call, Throwable t) {
                listener.onError(context.getString(R.string.error_connecting_to_server));
            }
        });
    }

    public void getCategoriesByWorkId(Context context, int workId, final OnGetCategoriesByWorkIdListener listener) {
        shopUseCase.getCategoriesByWorkId(workId).enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body();
                    listener.onSuccess(categories);
                } else {
                    listener.onError(context.getString(R.string.undefined_error));
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                listener.onError(context.getString(R.string.error_connecting_to_server));
            }
        });
    }

    public void getProfileById(Context context, int profileId, final OnGetProfileByIdListener listener) {
        shopUseCase.getProfileById(profileId).enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Profile profile = response.body();
                    listener.onSuccess(profile);
                } else {
                    listener.onFailure(context.getString(R.string.undefined_error));
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                listener.onFailure(context.getString(R.string.error_connecting_to_server));
            }
        });
    }

    public interface OnCategoryClickListener {
        void onSuccess(List<Category> category);
        void onFailure(String error);
    }

    public interface OnGetNewReleasesListener {
        void onSuccess(List<Work> work);
        void onFailure(String error);
    }

    public interface OnGetPopularListener {
        void onSuccess(List<Work> work);
        void onFailure(String error);
    }

    public interface OnGetWorksByTagListener {
        void onSuccess(WorkResponse work);
        void onFailure(String error);
    }

    public interface OnGetWorkByIdListener {
        void onSuccess(Work work);
        void onFailure(String error);
    }

    public interface OnGetChaptersByWorkIdListener{
        void onSuccess(List<Chapter> responses);
        void onError(String error);
    }

    public interface OnGetCategoriesByWorkIdListener{
        void onSuccess(List<Category> responses);
        void onError(String error);
    }

    public interface OnGetProfileByIdListener{
        void onSuccess(Profile profile);
        void onFailure(String error);
    }
}
