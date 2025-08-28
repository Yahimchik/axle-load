package com.mehatronics.axle_load.data.repository.impl;

import static com.mehatronics.axle_load.constants.BundleKeys.APP_LANGUAGE;

import com.mehatronics.axle_load.data.repository.LanguageRepository;
import com.mehatronics.axle_load.domain.entities.enums.AppLanguage;
import com.mehatronics.axle_load.domain.manager.SharedPreferencesManager;

import javax.inject.Inject;

public class LanguageRepositoryImpl implements LanguageRepository {
    private final SharedPreferencesManager prefs;

    @Inject
    public LanguageRepositoryImpl(SharedPreferencesManager prefs) {
        this.prefs = prefs;
    }

    @Override
    public AppLanguage getCurrentLanguage() {
        String code = prefs.get(APP_LANGUAGE, AppLanguage.EN.getCode());
        return AppLanguage.fromCode(code);
    }

    @Override
    public void saveLanguage(AppLanguage language) {
        prefs.put(APP_LANGUAGE, language.getCode());
    }
}