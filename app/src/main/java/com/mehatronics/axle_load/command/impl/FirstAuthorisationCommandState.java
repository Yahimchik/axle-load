package com.mehatronics.axle_load.command.impl;

import static com.mehatronics.axle_load.utils.constants.CommandsConstants.FIFTY_COMMAND;
import static com.mehatronics.axle_load.utils.constants.CommandsConstants.FIRST_COMMAND;

import android.bluetooth.BluetoothGatt;
import android.util.Log;

import com.mehatronics.axle_load.ble.handler.BluetoothGattCallbackHandler;
import com.mehatronics.axle_load.command.CommandStateHandler;

public class FirstAuthorisationCommandState implements CommandStateHandler {
    @Override
    public void handle(BluetoothGatt gatt, BluetoothGattCallbackHandler handler) {
        handler.setCommand(FIFTY_COMMAND, FIRST_COMMAND);
        handler.setCommandState(new SecondAuthorisationCommandState());
        Log.d("MyTag", "First authorisation command is sent");
    }
}
