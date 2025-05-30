package com.mehatronics.axle_load.state.impl;

import static com.mehatronics.axle_load.utils.constants.CommandsConstants.FIRST_COMMAND;

import android.bluetooth.BluetoothGatt;
import android.util.Log;

import com.mehatronics.axle_load.handler.BluetoothGattCallbackHandler;
import com.mehatronics.axle_load.state.CommandStateHandler;

public class PasswordCommandState implements CommandStateHandler {
    @Override
    public void handle(BluetoothGatt gatt, BluetoothGattCallbackHandler handler) {
        var sensorConfig = handler.getSensorConfigureLiveData().getValue();

        if (sensorConfig == null) {
            return;
        }

        if ((sensorConfig.getFlagSystem() & 0x00000080) == 0x00000080) {
            if ((sensorConfig.getFlagSystem() & 0x00000200) == 0x00000200) {
                handler.setCommand(FIRST_COMMAND, 0);
                Log.d("MyTag", "Password is checked " + sensorConfig.getFlagSystem());
            }
        }
        handler.setCommandState(new CommandAfterCheckingPassword());
    }
}
