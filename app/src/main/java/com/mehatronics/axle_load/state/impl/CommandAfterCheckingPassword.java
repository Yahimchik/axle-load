package com.mehatronics.axle_load.state.impl;

import static com.mehatronics.axle_load.utils.constants.CommandsConstants.FIFTY_COMMAND;
import static com.mehatronics.axle_load.utils.constants.CommandsConstants.NINE_COMMAND;

import android.bluetooth.BluetoothGatt;
import android.util.Log;

import com.mehatronics.axle_load.ble.handler.BluetoothGattCallbackHandler;
import com.mehatronics.axle_load.state.CommandStateHandler;

public class CommandAfterCheckingPassword implements CommandStateHandler {
    @Override
    public void handle(BluetoothGatt gatt, BluetoothGattCallbackHandler handler) {
        handler.setCommand(FIFTY_COMMAND, NINE_COMMAND);
        Log.d("MyTag", "Command after password checking is sent");
        handler.setCommandState(new FirstCommandState());
    }
}
