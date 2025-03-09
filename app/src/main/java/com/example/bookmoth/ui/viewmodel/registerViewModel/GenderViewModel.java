package com.example.bookmoth.ui.viewmodel.registerViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bookmoth.domain.model.Gender;

public class GenderViewModel extends ViewModel {
    private MutableLiveData<Gender> gender = new MutableLiveData<>();

    public LiveData<Gender> getGender() {
        return gender;
    }

    public void setGender(Gender gender){
        this.gender.setValue(gender);
    }
}
