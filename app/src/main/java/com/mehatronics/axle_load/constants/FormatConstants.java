package com.mehatronics.axle_load.constants;

import static com.mehatronics.axle_load.R.drawable.ic_flag_for_belarus;
import static com.mehatronics.axle_load.R.drawable.ic_flag_for_france;
import static com.mehatronics.axle_load.R.drawable.ic_flag_for_russia;
import static com.mehatronics.axle_load.R.drawable.ic_flag_united_kingdom;
import static com.mehatronics.axle_load.domain.entities.enums.AppLanguage.BE;
import static com.mehatronics.axle_load.domain.entities.enums.AppLanguage.EN;
import static com.mehatronics.axle_load.domain.entities.enums.AppLanguage.FR;
import static com.mehatronics.axle_load.domain.entities.enums.AppLanguage.RU;

import com.mehatronics.axle_load.domain.entities.enums.AppLanguage;

import java.util.Map;

public class FormatConstants {
    public static final String DATE_FORMAT = "%02d/%02d/%02d";
    public static final Map<AppLanguage, Integer> LANGUAGE_FLAGS = Map.of(
            EN, ic_flag_united_kingdom,
            RU, ic_flag_for_russia,
            BE, ic_flag_for_belarus,
            FR, ic_flag_for_france
    );
}