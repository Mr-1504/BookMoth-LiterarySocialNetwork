package com.example.bookmoth.ui.viewmodel.workview;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bookmoth.core.libraryutils.InnerCallback;
import com.example.bookmoth.data.repository.library.LibApiRepository;
import com.example.bookmoth.data.repository.library.ReadHistoryRepo;
import com.example.bookmoth.domain.model.library.Chapter;
import com.example.bookmoth.domain.model.library.Work;
import com.example.bookmoth.domain.usecase.library.GetChaptersOfWorkUseCase;
import com.example.bookmoth.domain.usecase.library.GetWorkByIdUseCase;
import com.example.bookmoth.domain.usecase.library.ReadHistoryUseCase;

import java.util.ArrayList;
import java.util.List;

public class WorkDetailsViewModel extends AndroidViewModel {
    public LiveData<List<Chapter>> getChapters() {return chapters;}

    private final GetChaptersOfWorkUseCase getChaptersOfWork = new GetChaptersOfWorkUseCase(new LibApiRepository());
    private final MutableLiveData<List<Chapter>> chapters = new MutableLiveData<>();
    public WorkDetailsViewModel(@NonNull Application application) {
        super(application);
    }
    private void fetchChapters(int work_id) {
        getChaptersOfWork.run(work_id, null, new InnerCallback<List<Chapter>>() {
            @Override
            public void onSuccess(List<Chapter> body) {
                chapters.setValue(body);
                makeReadChapters(body);
            }

            @Override
            public void onError(String errorMessage) {}
        });
    }

    private final ReadHistoryUseCase readHistory = new ReadHistoryUseCase(new ReadHistoryRepo(getApplication()));
    private final MutableLiveData<List<Integer>> readChapters = new MutableLiveData<>();
    public LiveData<List<Integer>> getReadChapters() {return readChapters;}
    private void makeReadChapters(List<Chapter> chapters) {
        List<Integer> reads = new ArrayList<>();
        for (Chapter c : chapters) {
            if (readHistory.isRead(c.getChapter_id())) reads.add(c.getChapter_id());
        }
        readChapters.setValue(reads);
        Log.d("READED MODEL", String.valueOf(reads.size()));
    }

    private final GetWorkByIdUseCase getWorkById = new GetWorkByIdUseCase(new LibApiRepository());
    private final MutableLiveData<Work> work = new MutableLiveData<>();

    public LiveData<Work> getWork() {return work;}
    private void fetchWork(int work_id) {
        getWorkById.run(work_id, new InnerCallback<Work>() {
            @Override
            public void onSuccess(Work body) {
                work.setValue(body);
                fetchChapters(work_id);
            }

            @Override
            public void onError(String errorMessage) {}
        });
    }

    public void fetchData(int work_id) {
        fetchWork(work_id);
    }
}
