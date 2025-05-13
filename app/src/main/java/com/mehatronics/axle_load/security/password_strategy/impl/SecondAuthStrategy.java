package com.mehatronics.axle_load.security.password_strategy.impl;

import static com.mehatronics.axle_load.security.password_strategy.CommandStrategyHelper.fillBufferWithHexString;
import static com.mehatronics.axle_load.constants.StringConstants.THIRD_CODE;

import com.mehatronics.axle_load.security.password_strategy.CommandStrategy;

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
