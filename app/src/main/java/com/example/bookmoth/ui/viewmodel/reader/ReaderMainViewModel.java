package com.example.bookmoth.ui.viewmodel.reader;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bookmoth.core.libraryutils.LibraryConst;
import com.example.bookmoth.core.libraryutils.InnerCallback;
import com.example.bookmoth.data.repository.library.LibApiRepository;
import com.example.bookmoth.data.repository.library.ReadHistoryRepo;
import com.example.bookmoth.domain.model.library.Chapter;
import com.example.bookmoth.domain.model.library.ReadHistory;
import com.example.bookmoth.domain.usecase.library.GetChapterContentUseCase;
import com.example.bookmoth.domain.usecase.library.ReadHistoryUseCase;

public class ReaderMainViewModel extends AndroidViewModel {
    private final GetChapterContentUseCase getChapterContent = new GetChapterContentUseCase(new LibApiRepository());
    private final MutableLiveData<String> markdownString = new MutableLiveData<>();
    private final MutableLiveData<String> message = new MutableLiveData<>();

    public ReaderMainViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<String> getMarkdownString() {return markdownString;}
    public LiveData<String> getMessage() {return message;}
    public void fetchChapterContent(Chapter chapter) {
        getChapterContent.run(LibraryConst.TEST_TOKEN, chapter.getContent_url(), new InnerCallback<String>() {
            @Override
            public void onSuccess(String body) {
                markdownString.setValue(body);
                recordRead(chapter);
            }

            @Override
            public void onError(String errorMessage) {
                message.setValue(errorMessage);
            }
        });
    }

    private final ReadHistoryUseCase readHistory = new ReadHistoryUseCase(new ReadHistoryRepo(getApplication()));
    private void recordRead(Chapter chapter) {
        ReadHistory rh = new ReadHistory(chapter.getChapter_id(), chapter.getWork_id(), chapter.getPost_date());
        readHistory.record(rh);
    }
}
