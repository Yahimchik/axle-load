package com.mehatronics.axle_load.helper;

import static com.mehatronics.axle_load.constants.BundleKeys.APP_LANGUAGE;
import static com.mehatronics.axle_load.constants.BundleKeys.APP_PREFERENCES;

import android.content.Context;
import android.content.res.Configuration;

import com.mehatronics.axle_load.domain.entities.enums.AppLanguage;

import java.util.Locale;

public class LocaleHelper {
    public static Context setLocale(Context context, String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration(context.getResources().getConfiguration());

        config.setLocale(locale);
        return context.createConfigurationContext(config);
    }

    public static Context setLocale(Context context, AppLanguage language) {
        return setLocale(context, language.getCode());
    }

    public static Context attachBaseContext(Context base) {
        String lang = base.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
                .getString(APP_LANGUAGE, AppLanguage.EN.getCode());
        return setLocale(base, lang);
    }
}