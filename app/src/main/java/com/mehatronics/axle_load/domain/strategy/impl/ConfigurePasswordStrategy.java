package com.mehatronics.axle_load.domain.strategy.impl;

import com.mehatronics.axle_load.data.repository.PasswordRepository;
import com.mehatronics.axle_load.domain.strategy.CommandStrategy;

import javax.inject.Inject;

public class ConfigurePasswordStrategy implements CommandStrategy {
    private final PasswordRepository passwordRepository;

    @Inject
    public ConfigurePasswordStrategy(PasswordRepository passwordRepository) {
        this.passwordRepository = passwordRepository;
    }

    @Override
    public void fillBuffer(byte[] buffer) {
        if (!passwordRepository.isPasswordStandart() && !passwordRepository.getNewPassword().isEmpty()) {
            for (int i = 4; i < 24; i++) {
                buffer[i] = 0x20;
            }

            char[] mass = passwordRepository.get().toCharArray();

            for (int i = 0; i < mass.length; i++) {
                buffer[4 + i] = (byte) mass[i];
            }

            char[] mass2 = passwordRepository.getNewPassword().toCharArray();

            for (int i = 0; i < mass2.length; i++) {
                buffer[14 + i] = (byte) mass2[i];
            }
        } else if (passwordRepository.isPasswordStandart() && !passwordRepository.getNewPassword().isEmpty()) {

            for (int i = 14; i < 24; i++) {
                buffer[i] = 0x20;
            }

            char[] mass = passwordRepository.getNewPassword().toCharArray();

            for (int i = 0; i < mass.length; i++) {
                buffer[14 + i] = (byte) mass[i];
            }

            passwordRepository.setPasswordStandart(false);
        } else {
            for (int i = 4; i < 14; i++) {
                buffer[i] = 0x20;
            }

            char[] mass = passwordRepository.get().toCharArray();

            for (int i = 0; i < mass.length; i++) {
                buffer[4 + i] = (byte) mass[i];
            }
        }
    }
}
