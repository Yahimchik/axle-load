package com.mehatronics.axle_load.localization.impl;

import static com.mehatronics.axle_load.constants.BundleKeys.APP_LANGUAGE;
import static com.mehatronics.axle_load.constants.BundleKeys.APP_PREFERENCES;
import static com.mehatronics.axle_load.domain.entities.enums.AppLanguage.EN;

import android.app.Application;
import android.content.Context;

import com.mehatronics.axle_load.helper.LocaleHelper;
import com.mehatronics.axle_load.localization.ResourceProvider;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AndroidResourceProvider implements ResourceProvider {
    private final Application application;

    @Inject
    public AndroidResourceProvider(Application application) {
        this.application = application;
    }

    @Override
    public String getString(int resId) {
        Context localizedContext = LocaleHelper.setLocale(application, getCurrentLanguage());
        return localizedContext.getString(resId);
    }

    @Override
    public String getString(int resId, Object... formatArgs) {
        Context localizedContext = LocaleHelper.setLocale(application, getCurrentLanguage());
        return localizedContext.getString(resId, formatArgs);
    }

    private String getCurrentLanguage() {
        return application
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
                .getString(APP_LANGUAGE, EN.getCode());
    }
}