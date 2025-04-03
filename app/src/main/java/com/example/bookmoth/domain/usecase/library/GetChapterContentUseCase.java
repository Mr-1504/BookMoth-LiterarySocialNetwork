package com.example.bookmoth.domain.usecase.library;

import com.example.bookmoth.core.libraryutils.InnerCallback;
import com.example.bookmoth.data.repository.library.LibApiRepository;

public class GetChapterContentUseCase {
    private LibApiRepository repo;
    public GetChapterContentUseCase(LibApiRepository repo) {this.repo = repo;}
    public void run(String token, String content_url, InnerCallback<String> callback) {
        repo.getChapterContent(token, content_url, callback);
    }
}
