package com.mehatronics.axle_load.domain.state.impl;

import static com.mehatronics.axle_load.constants.CommandsConstants.SEVEN_COMMAND;
import static com.mehatronics.axle_load.constants.CommandsConstants.TENTH_COMMAND;
import static com.mehatronics.axle_load.domain.entities.enums.ConnectStatus.WAITING;

import android.util.Log;

import com.mehatronics.axle_load.domain.handler.BluetoothGattCallbackHandler;
import com.mehatronics.axle_load.domain.state.CommandStateHandler;

public class ReadFromBtComMiniSecondPageCommandState implements CommandStateHandler {
    @Override
    public void handle(BluetoothGattCallbackHandler handler) {
        handler.setCommand(SEVEN_COMMAND, TENTH_COMMAND);
        Log.d("MyTag", "Read from BT_COM_MINI page 2");
        handler.getRepository().setStatus(WAITING);
        handler.setCommandState(new FinalCommandState());
    }
}