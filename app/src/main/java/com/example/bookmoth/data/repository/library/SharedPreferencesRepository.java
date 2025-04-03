package com.example.bookmoth.data.repository.library;

import android.content.SharedPreferences;

public class SharedPreferencesRepository {
    private static final String READER_FONTFAMILY = "reader_fontFamily";
    private static final String READER_TEXTSIZE = "reader_textSize";
    private static final String READER_BRIGHTNESS = "reader_brightness";
    public static final String READER_TEXTCOLOR = "reader_textcolor";
    public static final String READER_BACKGROUNDCOLOR = "reader_backgroundcolor";

    private final SharedPreferences setting;

    public SharedPreferencesRepository(SharedPreferences setting) {this.setting = setting;}

    public void setReaderFont(String fontName) {setting.edit().putString(READER_FONTFAMILY, fontName).apply();}
    public String getReaderFont() {return setting.getString(READER_FONTFAMILY, "alegreya");}
    public void setReaderTextSize(float textSize) {setting.edit().putFloat(READER_TEXTSIZE, textSize).apply();}
    public float getReaderTextSize() {return setting.getFloat(READER_TEXTSIZE, 14.0f);}
    public void setReaderBrightness(int brightness) {setting.edit().putInt(READER_BRIGHTNESS, brightness).apply();}
    public int getReaderBrightness() {return setting.getInt(READER_BRIGHTNESS, 100);}
    public void setReaderTextColor(int intARGB) {setting.edit().putInt(READER_TEXTCOLOR, intARGB).apply();}
    public int getReaderTextColor() {return setting.getInt(READER_TEXTCOLOR, 0xFF080808);}
    public void setReaderBackgroundColor(int intARGB) {setting.edit().putInt(READER_BACKGROUNDCOLOR, intARGB).apply();}
    public int getReaderBackgroundColor() {return setting.getInt(READER_BACKGROUNDCOLOR, 0xFFFFFFFF);}
}
