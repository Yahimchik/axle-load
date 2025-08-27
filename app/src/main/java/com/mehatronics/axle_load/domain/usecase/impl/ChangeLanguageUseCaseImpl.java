package com.mehatronics.axle_load.domain.usecase.impl;

import static com.mehatronics.axle_load.constants.BundleKeys.APP_LANGUAGE;

import android.app.Application;
import android.content.Context;

import com.mehatronics.axle_load.domain.usecase.ChangeLanguageUseCase;
import com.mehatronics.axle_load.domain.entities.enums.AppLanguage;
import com.mehatronics.axle_load.helper.LocaleHelper;
import com.mehatronics.axle_load.domain.manager.SharedPreferencesManager;

import javax.inject.Inject;

public class ChangeLanguageUseCaseImpl implements ChangeLanguageUseCase {
    private final SharedPreferencesManager prefs;
    private final Context context;

    @Inject
    public ChangeLanguageUseCaseImpl(SharedPreferencesManager prefs, Application app) {
        this.prefs = prefs;
        this.context = app;
    }

    @Override
    public AppLanguage getCurrentLanguage() {
        String langCode = prefs.get(APP_LANGUAGE, AppLanguage.EN.getCode());
        return AppLanguage.fromCode(langCode);
    }

    @Override
    public void setLanguage(AppLanguage language) {
        prefs.put(APP_LANGUAGE, language.getCode());
        LocaleHelper.setLocale(context, language.getCode());
    }
}