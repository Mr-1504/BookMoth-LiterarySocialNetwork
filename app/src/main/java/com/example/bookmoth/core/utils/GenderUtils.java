package com.example.bookmoth.core.utils;

import android.content.Context;
import com.example.bookmoth.R;
import com.example.bookmoth.domain.model.Gender;

public class GenderUtils {
    public static String getGenderDisplayName(Context context, Gender gender) {
        switch (gender) {
            case MALE:
                return context.getString(R.string.gender_male);
            case FEMALE:
                return context.getString(R.string.gender_female);
            case OTHER:
                return context.getString(R.string.gender_other);
            default:
                return "";
        }
    }
}
