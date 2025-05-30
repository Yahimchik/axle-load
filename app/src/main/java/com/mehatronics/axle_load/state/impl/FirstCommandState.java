package com.mehatronics.axle_load.state.impl;

import static com.mehatronics.axle_load.utils.constants.CommandsConstants.FIRST_COMMAND;
import android.bluetooth.BluetoothGatt;
import android.util.Log;

import com.mehatronics.axle_load.handler.BluetoothGattCallbackHandler;
import com.mehatronics.axle_load.state.CommandStateHandler;

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
     * @param gatt    объект BluetoothGatt для взаимодействия с BLE-устройством
     * @param handler обработчик GATT, управляющий состоянием и данными BLE
     */
    @Override
    public void handle(BluetoothGatt gatt, BluetoothGattCallbackHandler handler) {
        handler.setCommand(FIRST_COMMAND, handler.getTablePage());
        Log.d("MyTag", "First command sent");
        handler.setCommandState(new SecondCommandState());
    }
}
