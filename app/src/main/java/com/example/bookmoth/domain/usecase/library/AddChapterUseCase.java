package com.example.bookmoth.domain.usecase.library;

import com.example.bookmoth.core.libraryutils.InnerCallback;
import com.example.bookmoth.data.repository.library.LibApiRepository;
import com.example.bookmoth.domain.model.library.Chapter;

import java.io.File;

public class AddChapterUseCase {
    private LibApiRepository repo;
    public AddChapterUseCase(LibApiRepository repo) {this.repo = repo;}
    public void run(int work_id, File content, String filename, Chapter info, InnerCallback<String> callback) {
        repo.postChapter(work_id, content, filename, info, callback);
    }
}
