package com.example.bookmoth.domain.usecase.library;

import android.graphics.Bitmap;

import com.example.bookmoth.core.libraryutils.InnerCallback;
import com.example.bookmoth.data.repository.library.LibApiRepository;

public class GetWorkCoverUseCase {
    private final LibApiRepository repo;

    public GetWorkCoverUseCase(LibApiRepository repo) {
        this.repo = repo;
    }

    public void run(String cover_url, InnerCallback<Bitmap> callback) {
        repo.getWorkCover(cover_url, callback);
    }
}
