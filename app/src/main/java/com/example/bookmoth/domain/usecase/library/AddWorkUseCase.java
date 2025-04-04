package com.example.bookmoth.domain.usecase.library;

import com.example.bookmoth.core.libraryutils.InnerCallback;
import com.example.bookmoth.data.repository.library.LibApiRepository;
import com.example.bookmoth.domain.model.library.Work;

import java.io.File;

public class AddWorkUseCase {
    private LibApiRepository repo;
    public AddWorkUseCase(LibApiRepository repo) {this.repo = repo;}
    public void run(String token, File cover, Work work, InnerCallback<String> callback) {
        repo.postWork(token, cover, work, callback);
    }
}
