package com.mehatronics.axle_load.strategy.impl;

import static com.mehatronics.axle_load.helper.CommandStrategyHelper.fillBufferWithHexString;
import static com.mehatronics.axle_load.helper.CommandStrategyHelper.hashMd5;
import static com.mehatronics.axle_load.utils.constants.StringConstants.FIRST_CODE;
import static com.mehatronics.axle_load.utils.constants.StringConstants.SECOND_CODE;

import com.mehatronics.axle_load.strategy.CommandStrategy;

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
