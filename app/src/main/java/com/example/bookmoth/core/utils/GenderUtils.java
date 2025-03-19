package com.example.bookmoth.core.utils;

import static com.example.bookmoth.domain.model.profile.Gender.FEMALE;
import static com.example.bookmoth.domain.model.profile.Gender.MALE;
import static com.example.bookmoth.domain.model.profile.Gender.OTHER;

import android.content.Context;
import com.example.bookmoth.R;
import com.example.bookmoth.domain.model.profile.Gender;

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

    public static int getGenderIntValue(Gender gender){
        switch (gender){
            case MALE:
                return 0;
            case FEMALE:
                return 1;
            default:
                return 2;
        }
    }

    public static Gender getGenderFromInt(int gender){
        switch (gender){
            case 0:
                return MALE;
            case 1:
                return FEMALE;
            default:
                return OTHER;
        }
    }
}
