package com.example.bookmoth.core.utils;

import static com.example.bookmoth.domain.model.profile.Gender.FEMALE;
import static com.example.bookmoth.domain.model.profile.Gender.MALE;
import static com.example.bookmoth.domain.model.profile.Gender.OTHER;

import android.content.Context;

import com.example.bookmoth.R;
import com.example.bookmoth.domain.model.profile.Gender;

/**
 * Class tiện ích hỗ trợ xử lý giới tính trong ứng dụng.
 */
public class GenderUtils {

    /**
     * Lấy tên hiển thị của giới tính dựa trên giá trị {@link Gender}.
     *
     * @param context Context của ứng dụng, dùng để lấy chuỗi từ tài nguyên.
     * @param gender  Giá trị giới tính cần chuyển đổi.
     * @return Tên hiển thị của giới tính.
     */
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


    /**
     * Chuyển đổi giá trị giới tính {@link Gender} thành số nguyên.
     *
     * @param gender Giá trị giới tính cần chuyển đổi.
     * @return 0 nếu là MALE, 1 nếu là FEMALE, 2 nếu là OTHER.
     */
    public static int getGenderIntValue(Gender gender) {
        switch (gender) {
            case MALE:
                return 0;
            case FEMALE:
                return 1;
            default:
                return 2;
        }
    }


    /**
     * Chuyển đổi số nguyên thành giá trị giới tính {@link Gender}.
     *
     * @param gender Giá trị số nguyên đại diện cho giới tính.
     * @return {@link Gender#MALE} nếu giá trị là 0,
     * {@link Gender#FEMALE} nếu giá trị là 1,
     * {@link Gender#OTHER} nếu là giá trị khác.
     */
    public static Gender getGenderFromInt(int gender) {
        switch (gender) {
            case 0:
                return MALE;
            case 1:
                return FEMALE;
            default:
                return OTHER;
        }
    }

    public static int getGenderIntFromString(String gender) {
        switch (gender) {
            case "Nam":
                return 0;
            case "Nữ":
                return 1;
            default:
                return 2;
        }
    }
}
