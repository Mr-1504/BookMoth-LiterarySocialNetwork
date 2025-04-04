package com.example.bookmoth.ui.viewmodel.reader;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ReaderChapterListViewModel extends ViewModel {
    private final MutableLiveData<Integer> nowChapterIndex = new MutableLiveData<>();
    private int chapterCounts = 0;

    public void setChapterCounts(int count) {chapterCounts = count;}
    public int getChapterCounts() {return chapterCounts;}
    public void setNowChapterIndex(int index) {nowChapterIndex.setValue(index);}
    public LiveData<Integer> getNowChapterIndex() {return nowChapterIndex;}
}
