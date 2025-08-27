package com.mehatronics.axle_load.domain.state.impl;

import static com.mehatronics.axle_load.constants.CommandsConstants.FOURTH_COMMAND;
import static com.mehatronics.axle_load.constants.CommandsConstants.SEVENTY_SEVEN_COMMAND;
import static com.mehatronics.axle_load.domain.entities.enums.ConnectStatus.WAITING;

import android.util.Log;

import com.mehatronics.axle_load.domain.handler.BluetoothGattCallbackHandler;
import com.mehatronics.axle_load.domain.state.CommandStateHandler;

public class SaveToBTCOMMiniCommandState implements CommandStateHandler {
    @Override
    public void handle(BluetoothGattCallbackHandler handler) {
        handler.setCommand(SEVENTY_SEVEN_COMMAND, FOURTH_COMMAND);
        handler.saveToBTCOMMini();
        Log.d("MyTag", "Configuration sent");

        if (!handler.isSavedToBTCOMMini()) {
            handler.getRepository().setStatus(WAITING);
            handler.setSaveToMiniLive(true);
            handler.setCommandState(new FinalCommandState());
        }
    }
}