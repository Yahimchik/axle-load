package com.mehatronics.axle_load.domain.strategy.impl;

import com.mehatronics.axle_load.data.repository.DeviceRepository;
import com.mehatronics.axle_load.domain.strategy.CommandStrategy;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class StateNumberStrategy implements CommandStrategy {

    private final DeviceRepository repository;

    @Inject
    public StateNumberStrategy(DeviceRepository repository) {
        this.repository = repository;
    }

    @Override
    public void fillBuffer(byte[] buffer) {
        for (int i = 4; i < 14; i++) {
            buffer[i] = 0x20;
        }

        char[] chars = repository.getStateNumber().toCharArray();

        for (int i = 0; i < chars.length && i < 10; i++) {
            buffer[4 + i] = (byte) chars[i];
        }
    }
}