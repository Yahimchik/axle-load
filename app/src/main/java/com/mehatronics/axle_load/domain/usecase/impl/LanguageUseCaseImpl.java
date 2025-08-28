package com.mehatronics.axle_load.domain.usecase.impl;

import com.mehatronics.axle_load.data.repository.LanguageRepository;
import com.mehatronics.axle_load.domain.entities.enums.AppLanguage;
import com.mehatronics.axle_load.domain.usecase.LanguageUseCase;

import javax.inject.Inject;

public class LanguageUseCaseImpl implements LanguageUseCase {
    private final LanguageRepository repository;

    @Inject
    public LanguageUseCaseImpl(LanguageRepository repository) {
        this.repository = repository;
    }

    @Override
    public AppLanguage getCurrentLanguage() {
        return repository.getCurrentLanguage();
    }

    @Override
    public void saveLanguage(AppLanguage language) {
        repository.saveLanguage(language);
    }
}