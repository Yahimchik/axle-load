package com.mehatronics.axle_load.domain.usecase.impl;

import com.mehatronics.axle_load.data.repository.PasswordRepository;
import com.mehatronics.axle_load.domain.usecase.SubmitPasswordUseCase;

import javax.inject.Inject;

public class SubmitPasswordUseCaseImpl implements SubmitPasswordUseCase {

    private final PasswordRepository passwordRepository;

    @Inject
    public SubmitPasswordUseCaseImpl(PasswordRepository repository) {
        this.passwordRepository = repository;
    }

    @Override
    public void execute(String password) {
        passwordRepository.save(password);
        passwordRepository.setFlag(true);
    }
}
