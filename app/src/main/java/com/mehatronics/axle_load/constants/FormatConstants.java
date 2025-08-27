package com.mehatronics.axle_load.constants;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.domain.entities.enums.AppLanguage;

import java.util.Map;

public class FormatConstants {
    public static final String DATE_FORMAT = "%02d/%02d/%02d";
    public static final Map<AppLanguage, Integer> LANGUAGE_FLAGS = Map.of(
            AppLanguage.EN, R.drawable.ic_flag_united_kingdom,
            AppLanguage.RU, R.drawable.ic_flag_ru,
            AppLanguage.BE, R.drawable.ic_flag_for_flag_belarus,
            AppLanguage.FR, R.drawable.ic_flag_for_flag_france
    );
}