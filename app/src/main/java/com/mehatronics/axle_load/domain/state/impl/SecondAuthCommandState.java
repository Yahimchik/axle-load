package com.mehatronics.axle_load.domain.state.impl;

import static com.mehatronics.axle_load.constants.CommandsConstants.FIFTY_COMMAND;
import static com.mehatronics.axle_load.constants.CommandsConstants.SECOND_COMMAND;

import android.util.Log;

import com.mehatronics.axle_load.domain.handler.BluetoothGattCallbackHandler;
import com.mehatronics.axle_load.domain.state.CommandStateHandler;

public class SecondAuthCommandState implements CommandStateHandler {
    @Override
    public void handle(BluetoothGattCallbackHandler handler) {
        handler.setCommand(FIFTY_COMMAND, SECOND_COMMAND);
        Log.d("MyTag", "Second authorisation command is sent");
        handler.setCommandState(new CommandAfterAuth());
    }
}
