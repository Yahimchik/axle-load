package com.mehatronics.axle_load.domain.state.impl;

import static com.mehatronics.axle_load.constants.CommandsConstants.FIFTY_COMMAND;
import static com.mehatronics.axle_load.constants.CommandsConstants.FIRST_COMMAND;

import android.util.Log;

import com.mehatronics.axle_load.domain.handler.BluetoothGattCallbackHandler;
import com.mehatronics.axle_load.domain.state.CommandStateHandler;

public class FirstAuthCommandState implements CommandStateHandler {
    @Override
    public void handle(BluetoothGattCallbackHandler handler) {
        handler.setCommand(FIFTY_COMMAND, FIRST_COMMAND);
        Log.d("MyTag", "First authorisation command is sent");
        handler.setCommandState(new SecondAuthCommandState());
    }
}