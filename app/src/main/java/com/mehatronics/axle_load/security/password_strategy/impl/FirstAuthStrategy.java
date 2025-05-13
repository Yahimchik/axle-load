package com.mehatronics.axle_load.security.password_strategy.impl;

import static com.mehatronics.axle_load.security.password_strategy.CommandStrategyHelper.fillBufferWithHexString;
import static com.mehatronics.axle_load.security.password_strategy.CommandStrategyHelper.hashMd5;
import static com.mehatronics.axle_load.constants.StringConstants.FIRST_CODE;
import static com.mehatronics.axle_load.constants.StringConstants.SECOND_CODE;

import com.mehatronics.axle_load.security.password_strategy.CommandStrategy;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FirstAuthStrategy implements CommandStrategy {

    @Inject
    public FirstAuthStrategy() {
    }

    @Override
    public void fillBuffer(byte[] buffer) {
        String code = FIRST_CODE + SECOND_CODE;
        String hashedCode = hashMd5(code);
        fillBufferWithHexString(buffer, hashedCode);
    }
}
