package com.mehatronics.axle_load.data.repository;

import com.mehatronics.axle_load.domain.entities.enums.AppLanguage;

public interface LanguageRepository {
    AppLanguage getCurrentLanguage();
    void saveLanguage(AppLanguage language);
}

