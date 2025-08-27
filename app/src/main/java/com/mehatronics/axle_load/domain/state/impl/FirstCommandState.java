package com.mehatronics.axle_load.domain.state.impl;

import static com.mehatronics.axle_load.constants.CommandsConstants.FIRST_COMMAND;

import android.util.Log;

import com.mehatronics.axle_load.domain.handler.BluetoothGattCallbackHandler;
import com.mehatronics.axle_load.domain.state.CommandStateHandler;

/**
 * Начальное состояние "FIRST" в паттерне "Состояние" (State),
 * отправляющее первую управляющую команду BLE-устройству.
 * <p>
 * После выполнения переходит в состояние {@link SecondCommandState}.
 */
public class FirstCommandState implements CommandStateHandler {

    /**
     * Устанавливает первую команду (FIRST_COMMAND и ZERO_COMMAND_DECIMAL)
     * и переводит обработчик в следующее состояние {@link SecondCommandState}.
     *
     * @param handler обработчик GATT, управляющий состоянием и данными BLE
     */
    @Override
    public void handle(BluetoothGattCallbackHandler handler) {
        handler.setCommand(FIRST_COMMAND, handler.getTablePage());
        Log.d("MyTag", "First command sent");
        handler.setCommandState(new SecondCommandState());
        handler.resetPassword(false);
        handler.setPassword(false);
    }
}