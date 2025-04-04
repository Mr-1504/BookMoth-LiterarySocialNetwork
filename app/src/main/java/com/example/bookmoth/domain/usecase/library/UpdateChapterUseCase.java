package com.example.bookmoth.domain.usecase.library;

import com.example.bookmoth.core.libraryutils.InnerCallback;
import com.example.bookmoth.data.repository.library.LibApiRepository;
import com.example.bookmoth.domain.model.library.Chapter;

import java.io.File;

public class UpdateChapterUseCase {
    private LibApiRepository repo;
    public UpdateChapterUseCase(LibApiRepository repo) {this.repo = repo;}
    public void run(int chapter_id, File content, String filename, Chapter info, InnerCallback<String> callback) {
        repo.putChapter(chapter_id, content, filename, info, callback);
    }
}
