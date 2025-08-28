package com.mehatronics.axle_load.domain.usecase;

import com.mehatronics.axle_load.domain.entities.enums.AppLanguage;

public interface LanguageUseCase {
    AppLanguage getCurrentLanguage();

    void saveLanguage(AppLanguage language);
}