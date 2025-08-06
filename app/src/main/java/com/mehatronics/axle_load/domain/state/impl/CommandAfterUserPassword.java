package com.mehatronics.axle_load.domain.state.impl;

import static com.mehatronics.axle_load.constants.CommandsConstants.FIFTY_COMMAND;
import static com.mehatronics.axle_load.constants.CommandsConstants.NINE_COMMAND;

import android.util.Log;

import com.mehatronics.axle_load.domain.handler.BluetoothGattCallbackHandler;
import com.mehatronics.axle_load.domain.state.CommandStateHandler;

public class CommandAfterUserPassword implements CommandStateHandler {

    @Override
    public void handle(BluetoothGattCallbackHandler handler) {
        handler.setCommand(FIFTY_COMMAND, NINE_COMMAND);
        Log.d("MyTag", "Command with password is sent");
        handler.setCommandState(new FirstCommandState());
    }
}
