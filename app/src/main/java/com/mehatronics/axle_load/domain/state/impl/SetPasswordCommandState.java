package com.mehatronics.axle_load.domain.state.impl;

import static com.mehatronics.axle_load.constants.CommandsConstants.EIGHT_COMMAND;
import static com.mehatronics.axle_load.constants.CommandsConstants.FIFTY_COMMAND;

import com.mehatronics.axle_load.domain.handler.BluetoothGattCallbackHandler;
import com.mehatronics.axle_load.domain.state.CommandStateHandler;

public class SetPasswordCommandState implements CommandStateHandler {
    @Override
    public void handle(BluetoothGattCallbackHandler handler) {
        handler.setCommand(FIFTY_COMMAND, EIGHT_COMMAND);
        handler.setCommandState(new FirstCommandState());
    }
}