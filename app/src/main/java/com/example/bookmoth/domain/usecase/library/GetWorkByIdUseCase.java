package com.example.bookmoth.domain.usecase.library;

import com.example.bookmoth.domain.model.library.Work;
import com.example.bookmoth.data.repository.library.LibApiRepository;
import com.example.bookmoth.core.libraryutils.InnerCallback;

public class GetWorkByIdUseCase {
    private final LibApiRepository repo;

    public GetWorkByIdUseCase(LibApiRepository repo) {
        this.repo = repo;
    }

    public void run(int work_id, InnerCallback<Work> callback) {
        repo.getWorkById(work_id, callback);
    }
}
