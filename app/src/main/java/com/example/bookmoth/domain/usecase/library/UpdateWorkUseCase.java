package com.example.bookmoth.domain.usecase.library;

import com.example.bookmoth.core.libraryutils.InnerCallback;
import com.example.bookmoth.data.repository.library.LibApiRepository;
import com.example.bookmoth.domain.model.library.Work;

import java.io.File;

public class UpdateWorkUseCase {
    private LibApiRepository repo;
    public UpdateWorkUseCase(LibApiRepository repo) {this.repo = repo;}
    public void run(String token, int work_id, File cover, Work info, InnerCallback<String> callback) {
        repo.putWork(token, work_id, cover, info, callback);
    }
}
