package com.example.bookmoth.data.repository.library;

import android.content.Context;

import com.example.bookmoth.data.local.library.ReadHistorySQLiteHelper;
import com.example.bookmoth.domain.model.library.ReadHistory;

public class ReadHistoryRepo {
    private ReadHistorySQLiteHelper helper;
    public ReadHistoryRepo(Context context) {
        helper = new ReadHistorySQLiteHelper(context);
    }

    public void record(ReadHistory rh) {
        helper.record(rh);
    }

    public boolean isRead(int chapter_id) {
        return helper.isRead(chapter_id);
    }

    public boolean isOutdated(int chapter_id, String post_date) {
        return helper.isOutdated(chapter_id, post_date);
    }
}
