package com.mehatronics.axle_load.strategy.impl;

import com.mehatronics.axle_load.strategy.CommandStrategy;

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
