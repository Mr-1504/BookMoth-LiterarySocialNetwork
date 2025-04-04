package com.example.bookmoth.ui.viewmodel.reader;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ReaderScrollViewModel extends ViewModel {
        //Initialize scroll range
    private final MutableLiveData<Integer> activityHeight = new MutableLiveData<>();
    private final MutableLiveData<Integer> contentHeight = new MutableLiveData<>();
    public void setHeights(int activity, int content) {
        activityHeight.setValue(activity);
        contentHeight.setValue(content);
    }
    public LiveData<Integer> getActivityHeight() {return activityHeight;}
    public LiveData<Integer> getContentHeight() {return contentHeight;}
        //Sync scroll position
    private final MutableLiveData<Integer> viewScrollPosition = new MutableLiveData<>();
    private final MutableLiveData<Integer> barScrollPosition = new MutableLiveData<>();
    public void setViewScrollPosition(int pos) {viewScrollPosition.setValue(pos);}
    public LiveData<Integer> getViewScrollPosition() {return viewScrollPosition;}
    public void setBarScrollPosition(int pos) {barScrollPosition.setValue(pos);}
    public LiveData<Integer> getBarScrollPosition() {return barScrollPosition;}
}
