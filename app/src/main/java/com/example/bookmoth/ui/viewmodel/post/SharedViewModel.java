package com.example.bookmoth.ui.viewmodel.post;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private MutableLiveData<Boolean> buttonClicked = new MutableLiveData<>();

    public void setButtonClicked() {
        buttonClicked.setValue(true);
    }
    public LiveData<Boolean> getButtonClicked(){
        return buttonClicked;
    }
}
