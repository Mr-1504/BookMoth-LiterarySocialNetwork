package com.example.bookmoth.domain.usecase.library;

import com.example.bookmoth.core.libraryutils.InnerCallback;
import com.example.bookmoth.data.repository.library.LibApiRepository;

import okhttp3.ResponseBody;

public class GetWorkStatsUseCase {
    private LibApiRepository repo;
    public GetWorkStatsUseCase(LibApiRepository repo) {this.repo = repo;}
    public void run(int work_id, InnerCallback<ResponseBody> callback) {
        repo.getWorkStats(work_id, callback);
    }
}
