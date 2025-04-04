package com.example.bookmoth.domain.usecase.library;

import com.example.bookmoth.core.libraryutils.InnerCallback;
import com.example.bookmoth.data.repository.library.LibApiRepository;

public class RemoveWorkUseCase {
    private LibApiRepository repo;
    public RemoveWorkUseCase(LibApiRepository repo) {this.repo = repo;}
    public void run(int work_id, InnerCallback<String> callback) {
        repo.deleteWork(work_id, callback);
    }
}
