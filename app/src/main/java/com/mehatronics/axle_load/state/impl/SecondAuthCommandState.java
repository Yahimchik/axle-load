package com.mehatronics.axle_load.state.impl;

import static com.mehatronics.axle_load.utils.constants.CommandsConstants.FIFTY_COMMAND;
import static com.mehatronics.axle_load.utils.constants.CommandsConstants.SECOND_COMMAND;

import android.bluetooth.BluetoothGatt;
import android.util.Log;

import com.mehatronics.axle_load.ble.handler.BluetoothGattCallbackHandler;
import com.mehatronics.axle_load.state.CommandStateHandler;

public class SecondAuthCommandState implements CommandStateHandler {
    @Override
    public void handle(BluetoothGatt gatt, BluetoothGattCallbackHandler handler) {
        handler.setCommand(FIFTY_COMMAND, SECOND_COMMAND);
        handler.setCommandState(new CommandAfterAuth());
        Log.d("MyTag", "Second authorisation command is sent");
    }
}
