package com.mehatronics.axle_load.domain.state.impl;

import static com.mehatronics.axle_load.constants.CommandsConstants.FIFTH_COMMAND;
import static com.mehatronics.axle_load.constants.CommandsConstants.SEVENTY_SEVEN_COMMAND;

import com.mehatronics.axle_load.domain.handler.BluetoothGattCallbackHandler;
import com.mehatronics.axle_load.domain.state.CommandStateHandler;

public class WhriteToBtComMiniBeforeReadingConfig implements CommandStateHandler {
    @Override
    public void handle(BluetoothGattCallbackHandler handler) {
        handler.setCommand(SEVENTY_SEVEN_COMMAND, FIFTH_COMMAND);
        handler.setCommandState(new ReadFromBTCOMMiniCommandState());
    }
}
