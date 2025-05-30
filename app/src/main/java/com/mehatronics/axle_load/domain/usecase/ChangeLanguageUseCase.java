package com.mehatronics.axle_load.domain.usecase;

import com.mehatronics.axle_load.entities.enums.AppLanguage;

public interface ChangeLanguageUseCase {
    AppLanguage getCurrentLanguage();
    void setLanguage(AppLanguage language);
}
