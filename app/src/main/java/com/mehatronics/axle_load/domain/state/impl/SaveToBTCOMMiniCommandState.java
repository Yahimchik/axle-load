package com.mehatronics.axle_load.domain.state.impl;

import static com.mehatronics.axle_load.constants.CommandsConstants.FIRST_COMMAND;
import static com.mehatronics.axle_load.constants.CommandsConstants.FOURTH_COMMAND;
import static com.mehatronics.axle_load.constants.CommandsConstants.SEVENTY_SEVEN_COMMAND;

import android.util.Log;

import com.mehatronics.axle_load.domain.handler.BluetoothGattCallbackHandler;
import com.mehatronics.axle_load.domain.state.CommandStateHandler;

public class SaveToBTCOMMiniCommandState implements CommandStateHandler {
    @Override
    public void handle(BluetoothGattCallbackHandler handler) {
        handler.setCommand(SEVENTY_SEVEN_COMMAND, FOURTH_COMMAND);
        handler.saveToBTCOMMini();
        Log.d("MyTag", "Configuration sent");
        Log.d("MyTag", String.valueOf(handler.isSavedToBTCOMMini()));

        if (!handler.isSavedToBTCOMMini()) {
            handler.setSaveToMiniLive(true);
            handler.setCommandState(new FinalCommandState());
        }
    }
}
