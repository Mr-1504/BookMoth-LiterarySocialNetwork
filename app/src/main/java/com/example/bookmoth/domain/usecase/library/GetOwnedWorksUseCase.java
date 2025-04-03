package com.example.bookmoth.domain.usecase.library;

import com.example.bookmoth.core.libraryutils.InnerCallback;
import com.example.bookmoth.data.repository.library.LibApiRepository;
import com.example.bookmoth.domain.model.library.Work;

import java.util.List;

public class GetOwnedWorksUseCase {
    private LibApiRepository repo;
    public GetOwnedWorksUseCase(LibApiRepository repo) {this.repo = repo;}
    public void run(String token, InnerCallback<List<Work>> callback) {
        repo.getOwnedWorks(token, callback);
    }
}
