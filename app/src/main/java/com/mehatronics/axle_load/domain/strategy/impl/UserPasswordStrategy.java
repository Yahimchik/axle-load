package com.mehatronics.axle_load.domain.strategy.impl;

import android.util.Log;

import com.mehatronics.axle_load.data.repository.PasswordRepository;
import com.mehatronics.axle_load.domain.strategy.CommandStrategy;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserPasswordStrategy implements CommandStrategy {

    private final PasswordRepository passwordRepository;

    @Inject
    public UserPasswordStrategy(PasswordRepository passwordRepository) {
        this.passwordRepository = passwordRepository;
    }

    @Override
    public void fillBuffer(byte[] buffer) {
        if (!passwordRepository.shouldSendPassword()) {
            Log.d("MyTag", "Skip password buffer filling");
            return;
        }

        for (int i = 4; i < 14; i++) {
            buffer[i] = 0x20;
        }

        char[] chars = passwordRepository.get().toCharArray();

        for (int i = 0; i < chars.length && i < 10; i++) {
            buffer[4 + i] = (byte) chars[i];
        }
    }
}