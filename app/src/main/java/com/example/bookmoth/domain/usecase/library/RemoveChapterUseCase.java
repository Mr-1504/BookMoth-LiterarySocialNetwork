package com.example.bookmoth.domain.usecase.library;

import com.example.bookmoth.core.libraryutils.InnerCallback;
import com.example.bookmoth.data.repository.library.LibApiRepository;

public class RemoveChapterUseCase {
    private LibApiRepository repo;
    public RemoveChapterUseCase(LibApiRepository repo) {this.repo = repo;}
    public void run(int chapter_id, InnerCallback<String> callback) {
        repo.deleteChapter(chapter_id, callback);
    }
}
