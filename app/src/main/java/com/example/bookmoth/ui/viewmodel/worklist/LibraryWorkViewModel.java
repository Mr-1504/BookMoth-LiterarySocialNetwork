package com.example.bookmoth.ui.viewmodel.worklist;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bookmoth.core.libraryutils.LibraryConst;
import com.example.bookmoth.core.libraryutils.InnerCallback;
import com.example.bookmoth.data.repository.library.LibApiRepository;
import com.example.bookmoth.domain.model.library.Work;
import com.example.bookmoth.domain.usecase.library.GetCreatedWorksUseCase;
import com.example.bookmoth.domain.usecase.library.GetOwnedWorksUseCase;

import java.util.ArrayList;
import java.util.List;

public class LibraryWorkViewModel extends AndroidViewModel {
    private final MutableLiveData<String> errorString = new MutableLiveData<>();
    private final GetOwnedWorksUseCase getOwnedWorks = new GetOwnedWorksUseCase(new LibApiRepository());
    private final GetCreatedWorksUseCase getCreatedWorks = new GetCreatedWorksUseCase(new LibApiRepository());
    private final MutableLiveData<List<Work>> ownedWorks = new MutableLiveData<>();
    private final MutableLiveData<List<Work>> createdWorks = new MutableLiveData<>();

    public LibraryWorkViewModel(@NonNull Application application) {
        super(application);
        ownedWorks.setValue(new ArrayList<>());
        createdWorks.setValue(new ArrayList<>());
    }

    public LiveData<List<Work>> getOwnedWorks() {return ownedWorks;}
    public LiveData<List<Work>> getCreatedWorks() {return createdWorks;}

    public void fetchOwnedWorks() {
        getOwnedWorks.run(LibraryConst.TEST_TOKEN, new InnerCallback<List<Work>>() {
            @Override
            public void onSuccess(List<Work> body) {
                ownedWorks.setValue(body);
            }

            @Override
            public void onError(String errorMessage) {
//                ErrorDialog.showError(getApplication(), errorMessage);
                errorString.setValue(errorMessage);
            }
        });
    }
    public void fetchCreatedWorks() {
        getCreatedWorks.run(LibraryConst.TEST_TOKEN, new InnerCallback<List<Work>>() {
            @Override
            public void onSuccess(List<Work> body) {
                createdWorks.setValue(body);
            }

            @Override
            public void onError(String errorMessage) {
//                ErrorDialog.showError(getApplication(), errorMessage);
                errorString.setValue(errorMessage);
            }
        });
    }

    public LiveData<String> getErrorString() {return errorString;}
}
