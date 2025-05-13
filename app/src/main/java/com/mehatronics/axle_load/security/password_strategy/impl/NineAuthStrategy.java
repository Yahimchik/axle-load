package com.mehatronics.axle_load.security.password_strategy.impl;

import com.mehatronics.axle_load.security.password_strategy.CommandStrategy;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class NineAuthStrategy implements CommandStrategy {

    @Inject
    public NineAuthStrategy() {
    }

    @Override
    public void fillBuffer(byte[] buffer) {

    }
}
