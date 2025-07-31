package com.mehatronics.axle_load.domain.state.impl;

import static com.mehatronics.axle_load.constants.CommandsConstants.FIRST_COMMAND;
import static com.mehatronics.axle_load.constants.CommandsConstants.ZERO_COMMAND_DECIMAL;

import android.bluetooth.BluetoothGatt;
import android.util.Log;

import com.mehatronics.axle_load.domain.handler.BluetoothGattCallbackHandler;
import com.mehatronics.axle_load.domain.state.CommandStateHandler;

public class PasswordCommandState implements CommandStateHandler {
    @Override
    public void handle(BluetoothGatt gatt, BluetoothGattCallbackHandler handler) {
        var passwordRepository = handler.getPasswordRepository();

        var sensorConfig = handler.getSensorConfigureLiveData().getValue();
        String currentMac = handler.getCurrentMac();

        if (sensorConfig == null) {
            Log.d("MyTag", "Config отсутствует. Ждём актуализации.");
            return;
        }

        if (!currentMac.equals(sensorConfig.getMac())) {
            Log.d("MyTag", "Config неактуален. MAC из config: " + sensorConfig.getMac()
                    + " <> ожидалось: " + currentMac);
            return;
        }

        boolean isPasswordProtected = (sensorConfig.getFlagSystem() & 0x00000080) == 0x00000080;
        boolean isPasswordSet = (sensorConfig.getFlagSystem() & 0x00000200) == 0x00000200;

        if (isPasswordProtected) {
            if (isPasswordSet) {
                Log.d("MyTag", "Пароль уже установлен.");
                handler.setCommand(FIRST_COMMAND, ZERO_COMMAND_DECIMAL);
                passwordRepository.setFlag(false);
                handler.setCommandState(new CommandAfterCheckingPassword());
            } else {
                String password = passwordRepository.get();
                if (password.isBlank()) {
                    Log.d("MyTag", "Пароль отсутствует. Показываем диалог.");
                    passwordRepository.setFlag(true);
                    handler.notifyPasswordRequired();
                } else {
                    Log.d("MyTag", "Пароль есть, продолжаем.");
                    handler.setCommandState(new CommandAfterUserPassword());
                }
            }
        } else {
            Log.d("MyTag", "Пароль не требуется.");
            handler.setCommandState(new CommandAfterCheckingPassword());
        }
    }
}
