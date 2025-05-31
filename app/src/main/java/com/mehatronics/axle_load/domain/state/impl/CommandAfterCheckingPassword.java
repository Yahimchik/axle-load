package com.mehatronics.axle_load.domain.state.impl;

import static com.mehatronics.axle_load.constants.CommandsConstants.FIFTY_COMMAND;
import static com.mehatronics.axle_load.constants.CommandsConstants.NINE_COMMAND;

import android.bluetooth.BluetoothGatt;
import android.util.Log;

import com.mehatronics.axle_load.domain.handler.BluetoothGattCallbackHandler;
import com.mehatronics.axle_load.domain.state.CommandStateHandler;

public class CommandAfterCheckingPassword implements CommandStateHandler {
    @Override
    public void handle(BluetoothGatt gatt, BluetoothGattCallbackHandler handler) {
        handler.setCommand(FIFTY_COMMAND, NINE_COMMAND);
        Log.d("MyTag", "Command after password checking is sent");
        handler.setCommandState(new FirstCommandState());
    }
}
