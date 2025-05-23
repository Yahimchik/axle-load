package com.mehatronics.axle_load.state.impl;

import static com.mehatronics.axle_load.utils.constants.CommandsConstants.SEVENTY_ONE_COMMAND;

import android.bluetooth.BluetoothGatt;

import com.mehatronics.axle_load.ble.handler.BluetoothGattCallbackHandler;
import com.mehatronics.axle_load.state.CommandStateHandler;

public class SaveTableCommand implements CommandStateHandler {
    @Override
    public void handle(BluetoothGatt gatt, BluetoothGattCallbackHandler handler) {
        handler.setCommand(SEVENTY_ONE_COMMAND, handler.getTablePage());
        handler.saveTableToSensor();
        if (!handler.isTableSaved()) {
            handler.setCommandState(new FinalCommandState());
        }
    }
}
