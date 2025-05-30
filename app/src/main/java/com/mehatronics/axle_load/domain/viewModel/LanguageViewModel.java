package com.mehatronics.axle_load.domain.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mehatronics.axle_load.domain.usecase.ChangeLanguageUseCase;
import com.mehatronics.axle_load.domain.usecase.ChangeLanguageUseCaseImpl;
import com.mehatronics.axle_load.entities.enums.AppLanguage;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class LanguageViewModel extends ViewModel {
    private final MutableLiveData<AppLanguage> currentLang = new MutableLiveData<>();
    private final ChangeLanguageUseCase changeLanguageUseCase;

    @Inject
    public LanguageViewModel(ChangeLanguageUseCase changeLanguageUseCase) {
        this.changeLanguageUseCase = changeLanguageUseCase;
        currentLang.setValue(changeLanguageUseCase.getCurrentLanguage());
    }

    public void toggleLanguage() {
        AppLanguage newLang = currentLang.getValue() == AppLanguage.EN ? AppLanguage.RU : AppLanguage.EN;
        changeLanguageUseCase.setLanguage(newLang);
        currentLang.setValue(newLang);
    }

    public MutableLiveData<AppLanguage> getCurrentLang() {
        return currentLang;
    }
}

