package com.mehatronics.axle_load.domain.strategy.impl;

import android.util.Log;

import com.mehatronics.axle_load.domain.entities.PasswordHolder;
import com.mehatronics.axle_load.domain.strategy.CommandStrategy;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserPasswordStrategy implements CommandStrategy {

    @Inject
    public UserPasswordStrategy() {

    }

    @Override
    public void fillBuffer(byte[] buffer) {
        if (!PasswordHolder.getInstance().shouldSendPassword()) {
            Log.d("MyTag", "Skip password buffer filling");
            return;
        }

        String password = PasswordHolder.getInstance().getPassword();
        for (int i = 4; i < 14; i++) {
            buffer[i] = 0x20;
        }

        char[] chars = password.toCharArray();
        for (int i = 0; i < chars.length && i < 10; i++) {
            buffer[4 + i] = (byte) chars[i];
        }

        PasswordHolder.getInstance().clear(); // сбросить после отправки
    }
}