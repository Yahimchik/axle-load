package com.mehatronics.axle_load.ble.strategy.impl;

import static com.mehatronics.axle_load.ble.strategy.CommandStrategyHelper.fillBufferWithHexString;
import static com.mehatronics.axle_load.utils.constants.StringConstants.THIRD_CODE;

import com.mehatronics.axle_load.ble.strategy.CommandStrategy;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SecondAuthStrategy implements CommandStrategy {

    @Inject
    public SecondAuthStrategy() {
    }

    @Override
    public void fillBuffer(byte[] buffer) {
        fillBufferWithHexString(buffer, THIRD_CODE);
    }
}
