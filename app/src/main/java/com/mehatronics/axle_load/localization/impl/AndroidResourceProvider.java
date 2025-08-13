package com.mehatronics.axle_load.localization.impl;

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
        return android.preference.PreferenceManager
                .getDefaultSharedPreferences(application)
                .getString("app_lang", "en");
    }
}

