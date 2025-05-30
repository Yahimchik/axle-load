package com.mehatronics.axle_load.domain.usecase;

import android.app.Application;
import android.content.Context;

import com.mehatronics.axle_load.entities.enums.AppLanguage;
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
        String langCode = prefs.get("app_lang", AppLanguage.EN.getCode());
        return AppLanguage.fromCode(langCode);
    }

    @Override
    public void setLanguage(AppLanguage language) {
        prefs.put("app_lang", language.getCode());
        LocaleHelper.setLocale(context, language.getCode());
    }
}
