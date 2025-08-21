package com.mehatronics.axle_load.domain.state.impl;

import static com.mehatronics.axle_load.constants.CommandsConstants.FIRST_COMMAND;
import static com.mehatronics.axle_load.constants.CommandsConstants.FOURTH_COMMAND;
import static com.mehatronics.axle_load.constants.CommandsConstants.NINE_COMMAND;
import static com.mehatronics.axle_load.constants.CommandsConstants.SECOND_COMMAND;
import static com.mehatronics.axle_load.constants.CommandsConstants.SEVENTY_SEVEN_COMMAND;
import static com.mehatronics.axle_load.constants.CommandsConstants.SEVEN_COMMAND;
import static com.mehatronics.axle_load.domain.entities.enums.ConnectStatus.READ;
import static com.mehatronics.axle_load.domain.entities.enums.ConnectStatus.WAITING;

import android.util.Log;

import com.mehatronics.axle_load.domain.handler.BluetoothGattCallbackHandler;
import com.mehatronics.axle_load.domain.state.CommandStateHandler;

public class ReadFromBTCOMMiniCommandState implements CommandStateHandler {
    @Override
    public void handle(BluetoothGattCallbackHandler handler) {
        handler.setCommand(SEVEN_COMMAND, NINE_COMMAND);
        Log.d("MyTag", "Read from btcom");
        handler.getRepository().setStatus(WAITING);
        Log.d("MyTag", handler.getRepository().getStatus().name());
        handler.setCommandState(new FinalCommandState());
    }
}
