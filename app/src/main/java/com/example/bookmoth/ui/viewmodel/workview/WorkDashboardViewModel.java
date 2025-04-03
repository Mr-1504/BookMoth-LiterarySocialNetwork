package com.example.bookmoth.ui.viewmodel.workview;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.example.bookmoth.core.libraryutils.AppConst;
import com.example.bookmoth.core.libraryutils.InnerCallback;
import com.example.bookmoth.data.repository.library.LibApiRepository;
import com.example.bookmoth.domain.model.library.Chapter;
import com.example.bookmoth.domain.model.library.Work;
import com.example.bookmoth.domain.usecase.library.GetChaptersOfWorkUseCase;
import com.example.bookmoth.domain.usecase.library.GetWorkByIdUseCase;
import com.example.bookmoth.domain.usecase.library.GetWorkStatsUseCase;
import com.example.bookmoth.domain.usecase.library.RemoveChapterUseCase;
import com.example.bookmoth.domain.usecase.library.RemoveWorkUseCase;

import java.util.List;

import okhttp3.ResponseBody;

public class WorkDashboardViewModel extends ViewModel {
    private final GetWorkStatsUseCase getWorkStats = new GetWorkStatsUseCase(new LibApiRepository());
    private final GetChaptersOfWorkUseCase getChaptersOfWork = new GetChaptersOfWorkUseCase(new LibApiRepository());
    private final RemoveWorkUseCase removeWork = new RemoveWorkUseCase(new LibApiRepository());
    private final RemoveChapterUseCase removeChapter = new RemoveChapterUseCase(new LibApiRepository());

    private final MutableLiveData<String> message = new MutableLiveData<>();
    public static final int ACTION_REMWORK = 0;
    public static final int ACTION_REMCHAP = 1;
    private int action;

    public LiveData<String> getMessage() {return message;}
    public int getAction() {return action;}

    private final MutableLiveData<WorkStats> workStats = new MutableLiveData<>();
    public LiveData<WorkStats> getWorkStats() {return workStats;}
    private void setWorkStats(WorkStats stats) {workStats.setValue(stats);}

    private final MutableLiveData<List<Chapter>> chapters = new MutableLiveData<>();
    public LiveData<List<Chapter>> getChapters() {return chapters;}
    private void setChapters(List<Chapter> chapters) {this.chapters.setValue(chapters);}

    public void fetchWork(int work_id) {
        getWorkStats.run(AppConst.TEST_TOKEN, work_id, new InnerCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody body) {
                try {
                    String json = body.string();
                    Gson gson = new Gson();
                    WorkStats stats = gson.fromJson(json, WorkStats.class);
                    setWorkStats(stats);
                } catch (Exception ignored) {}
                fetchChapters(work_id);
            }

            @Override
            public void onError(String errorMessage) {}
        });
    }

    private void fetchChapters(int work_id) {
        getChaptersOfWork.run(work_id, null, new InnerCallback<List<Chapter>>() {
            @Override
            public void onSuccess(List<Chapter> body) {
                setChapters(body);
                fetchWorkInfo(work_id);
            }

            @Override
            public void onError(String errorMessage) {}
        });
    }

    public void removeWork(int work_id) {
        action = ACTION_REMWORK;
        removeWork.run(AppConst.TEST_TOKEN, work_id, new InnerCallback<String>() {
            @Override
            public void onSuccess(String body) {
                message.setValue("");
            }

            @Override
            public void onError(String errorMessage) {
                message.setValue(errorMessage);
            }
        });
    }

    public void removeChaper(int chapter_id) {
        action = ACTION_REMCHAP;
        removeChapter.run(AppConst.TEST_TOKEN, chapter_id, new InnerCallback<String>() {
            @Override
            public void onSuccess(String body) {
                message.setValue("");
            }

            @Override
            public void onError(String errorMessage) {
                message.setValue(errorMessage);
            }
        });
    }

    public static class WorkStats {
        private int work_id;
        private int view_count;
        private int follow_count;
        private String last_update;
        private float price;

        public int getView_count() {
            return view_count;
        }

        public int getFollow_count() {
            return follow_count;
        }

        public String getLast_update() {
            return last_update;
        }

        public float getPrice() {
            return price;
        }
    }

    private final GetWorkByIdUseCase getWorkById = new GetWorkByIdUseCase(new LibApiRepository());
    private final MutableLiveData<Work> work = new MutableLiveData<>();
    public LiveData<Work> getWork() {return work;}
    private void fetchWorkInfo(int work_id) {
        getWorkById.run(work_id, new InnerCallback<Work>() {
            @Override
            public void onSuccess(Work body) {
                work.setValue(body);
            }

            @Override
            public void onError(String errorMessage) {}
        });
    }
}
