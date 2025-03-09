package com.example.bookmoth.ui.viewmodel.registerViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NameViewModel extends ViewModel {
    private MutableLiveData<String> lastName = new MutableLiveData<>();
    private MutableLiveData<String> firstName = new MutableLiveData<>();

    public LiveData<String> getLastName() {
        return lastName;
    }
    public LiveData<String> getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName.setValue(lastName);
    }

    public void setFirstName(String firstName) {
        this.firstName.setValue(firstName);
    }
}
