package com.example.bookmoth.ui.viewmodel.reader;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bookmoth.domain.usecase.library.ManageSettingUseCase;

public class ReaderTextFormatViewModel extends AndroidViewModel {
        //text size
    private final MutableLiveData<Float> textSize = new MutableLiveData<>();

    public ReaderTextFormatViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Float> getTextSize() {return textSize;}
    public void setTextSize(float value) {
        textSize.setValue(value);
        setting.setReaderTextSize(value);
    }
        //font family
    private final MutableLiveData<Integer> fontFamily = new MutableLiveData<>();
    public LiveData<Integer> getFontFamily() {return fontFamily;}
    public void setFontFamily(int fontRid) {
        fontFamily.setValue(fontRid);
        setting.setReaderFont(getApplication().getResources().getResourceName(fontRid));
    }

    private ManageSettingUseCase setting;
    public void loadSettings(ManageSettingUseCase setting) {
        this.setting = setting;
        setFontFamily(getApplication().getResources().getIdentifier(setting.getReaderFont(), "font", getApplication().getPackageName()));
        setTextSize(setting.getReaderTextSize());
    }
}
