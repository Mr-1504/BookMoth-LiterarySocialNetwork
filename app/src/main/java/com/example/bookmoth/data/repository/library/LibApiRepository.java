package com.example.bookmoth.data.repository.library;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.example.bookmoth.domain.model.library.Chapter;
import com.example.bookmoth.domain.model.library.Work;
import com.example.bookmoth.data.remote.library.LibApiService;
import com.example.bookmoth.data.remote.utils.RetrofitClient;
import com.example.bookmoth.core.libraryutils.InnerCallback;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LibApiRepository {
    private LibApiService api;

    public LibApiRepository() {
        api = RetrofitClient.getLibraryRetrofit().create(LibApiService.class);
    }

    public void getChapterContent(String content_url, InnerCallback<String> callback) {
        api.getChapterContent(content_url).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Executors.newSingleThreadExecutor().execute(() -> { //Executor open a new thread to run heavy task
                        try {
                            String content = response.body().string();
                            new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(content)); //Handler wait for Executor result (post) to push it to main thread (Looper.getMainLooper())
                        } catch (IOException e) {
                            new Handler(Looper.getMainLooper()).post(() -> callback.onError(e.toString()));
                        }
                    });
                } else {
                    try {
                        callback.onError(response.errorBody().string());
                    } catch (IOException e) {
                        callback.onError(e.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onError(t.toString());
            }
        });
    }

    public void getWorkCover(String cover_url, InnerCallback<Bitmap> callback) {
        if (cover_url.isBlank()) return;
        api.getWorkCover(cover_url).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    byte[] imageData;
                    try {
                        imageData = response.body().bytes();
                        callback.onSuccess(BitmapFactory.decodeByteArray(imageData, 0, imageData.length));
                    } catch (IOException e) {
                        callback.onError(e.toString());
                    }
                } else {
                    try {
                        callback.onError(response.errorBody().string());
                    } catch (IOException e) {
                        callback.onError(e.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onError(t.toString());
            }
        });
    }

    public void getOwnedWorks(InnerCallback<List<Work>> callback) {
        api.getOwnedWorks().enqueue(NoProcessingCallback.make(callback));
    }

    public void getCreatedWorks(InnerCallback<List<Work>> callback) {
        api.getCreatedWorks().enqueue(NoProcessingCallback.make(callback));
    }

    public void getWorkById(int work_id, InnerCallback<Work> callback) {
        api.getWorkById(work_id).enqueue(NoProcessingCallback.make(callback));
    }

    public void getWorks(Map<String, String> args, InnerCallback<List<Work>> callback) {
        if (args == null) {
            args = Collections.emptyMap();
        }
        api.getWorks(args).enqueue(NoProcessingCallback.make(callback));
    }

    public void getChaptersOfWork(int work_id, Map<String, String> args, InnerCallback<List<Chapter>> callback) {
        if (args == null) args = Collections.emptyMap();
        api.getChaptersOfWork(work_id, args).enqueue(NoProcessingCallback.make(callback));
    }

    public void getChapterById(int chapter_id, InnerCallback<Chapter> callback) {

    }

    public void postWork(File cover, Work info, InnerCallback<String> callback) {
        RequestBody resFile = cover != null ?
                RequestBody.create(MediaType.parse("image/*"), cover) :
                null;
        MultipartBody.Part coverFormatRes = resFile != null ?
                MultipartBody.Part.createFormData("cover", "cover", resFile) :
                null;
        RequestBody resJson = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(info));
        api.postWork(coverFormatRes, resJson).enqueue(MessageOnlyCallback.make(callback));
    }

    public void postChapter(int work_id, File content, String filename, Chapter info, InnerCallback<String> callback) {
        RequestBody resFile = content != null ?
                RequestBody.create(MediaType.parse("application/octet-stream"), content) :
                null;
        if (resFile == null) return;
        MultipartBody.Part contentFormatRes = MultipartBody.Part.createFormData("content", filename, resFile);
        RequestBody resJson = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(info));
        api.postChapter(work_id, contentFormatRes, resJson).enqueue(MessageOnlyCallback.make(callback));
    }

    public void getWorkStats(int work_id, InnerCallback<ResponseBody> callback) {
        api.getWorkStats(work_id).enqueue(NoProcessingCallback.make(callback));
    }

    public void deleteWork(int work_id, InnerCallback<String> callback) {
        api.deleteWork(work_id).enqueue(MessageOnlyCallback.make(callback));
    }

    public void putWork(int work_id, File cover, Work info, InnerCallback<String> callback) {
        RequestBody resFile = cover != null ?
                RequestBody.create(MediaType.parse("image/*"), cover) :
                null;
        MultipartBody.Part coverFormatRes = resFile != null ?
                MultipartBody.Part.createFormData("cover", "cover", resFile) :
                null;
        RequestBody resJson = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(info));
        api.putWork(work_id, coverFormatRes, resJson).enqueue(MessageOnlyCallback.make(callback));
    }

    public void deleteChapter(int chapter_id, InnerCallback<String> callback) {
        api.deleteChapter(chapter_id).enqueue(MessageOnlyCallback.make(callback));
    }

    public void putChapter(int chapter_id, File content, String filename, Chapter info, InnerCallback<String> callback) {
        RequestBody resFile = content != null ?
                RequestBody.create(MediaType.parse("application/octet-stream"), content) :
                null;
        MultipartBody.Part contentFormatRes = resFile != null ?
                MultipartBody.Part.createFormData("content", filename, resFile) :
                null;
        RequestBody resJson = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(info));
        api.putChapter(chapter_id, contentFormatRes, resJson).enqueue(MessageOnlyCallback.make(callback));
    }
}
