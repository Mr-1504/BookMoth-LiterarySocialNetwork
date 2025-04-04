package com.example.bookmoth.domain.usecase.library;

import com.example.bookmoth.data.repository.library.LibApiRepository;
import com.example.bookmoth.domain.model.library.Work;
import com.example.bookmoth.core.libraryutils.InnerCallback;

import java.util.List;
import java.util.Map;

public class GetWorksUseCase {
    private final LibApiRepository repo;

    public GetWorksUseCase(LibApiRepository repo) {
        this.repo = repo;
    }

    public void run(Map<String, String> args, InnerCallback<List<Work>> callback) {
        repo.getWorks(args, callback);
    }
}
