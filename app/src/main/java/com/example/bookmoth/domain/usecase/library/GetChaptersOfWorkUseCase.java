package com.example.bookmoth.domain.usecase.library;

import com.example.bookmoth.core.libraryutils.InnerCallback;
import com.example.bookmoth.data.repository.library.LibApiRepository;
import com.example.bookmoth.domain.model.library.Chapter;

import java.util.List;
import java.util.Map;

public class GetChaptersOfWorkUseCase {
    private LibApiRepository repo;
    public GetChaptersOfWorkUseCase(LibApiRepository repo) {this.repo = repo;}
    public void run(int work_id, Map<String, String> args, InnerCallback<List<Chapter>> callback) {
        repo.getChaptersOfWork(work_id, args, callback);
    }
}
