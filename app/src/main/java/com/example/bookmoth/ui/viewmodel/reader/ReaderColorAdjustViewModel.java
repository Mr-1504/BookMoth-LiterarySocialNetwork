package com.example.bookmoth.ui.viewmodel.reader;

import android.graphics.Color;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bookmoth.domain.usecase.library.ManageSettingUseCase;

public class ReaderColorAdjustViewModel extends ViewModel {
    private final MutableLiveData<Integer> brightness = new MutableLiveData<>();
    private final MutableLiveData<Integer> colorTint = new MutableLiveData<>();
    private final MutableLiveData<Integer> textColor = new MutableLiveData<>();
    private final MutableLiveData<Integer> backgroundColor = new MutableLiveData<>();

    public MutableLiveData<Integer> getBrightness() {return brightness;}
    public MutableLiveData<Integer> getColorTint() {return colorTint;}
    public MutableLiveData<Integer> getTextColor() {return textColor;}
    public MutableLiveData<Integer> getBackgroundColor() {return backgroundColor;}

    public void setBrightness(int value) {
        brightness.setValue(value);
        setting.setReaderBrightness(value);
    }
    public void setColorTint(int value) {colorTint.setValue(value);}
    public void setTextColor(int intARGB) {
        textColor.setValue(intARGB);
        setting.setReaderTextColor(intARGB);
    }
    public void setBackgroundColor(int intARGB) {
        backgroundColor.setValue(intARGB);
        setting.setReaderBackgroundColor(intARGB);
    }

    private ManageSettingUseCase setting;
    public void loadSettings(ManageSettingUseCase setting) {
        setColorTint(Color.parseColor("#FF000000"));

        this.setting = setting;
        setBrightness(setting.getReaderBrightness());
        setTextColor(setting.getReaderTextColor());
        setBackgroundColor(setting.getReaderBackgroundColor());
    }
}
