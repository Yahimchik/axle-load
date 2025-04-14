package com.mehatronics.axle_load.command.impl;

import static com.mehatronics.axle_load.utils.constants.CommandsConstants.SECOND_COMMAND;
import static com.mehatronics.axle_load.utils.constants.CommandsConstants.SEVEN_COMMAND;

import android.bluetooth.BluetoothGatt;

import com.mehatronics.axle_load.ble.handler.BluetoothGattCallbackHandler;
import com.mehatronics.axle_load.command.CommandStateHandler;

/**
 * Финальное состояние "FINAL" в паттерне "Состояние" (State),
 * устанавливающее последнюю команду для BLE-устройства.
 *
 * Если требуется отправка конфигурации, состояние переходит к {@link ConfigureCommandState}.
 */
public class FinalCommandState implements CommandStateHandler {

    /**
     * Устанавливает финальную команду (SEVEN_COMMAND и SECOND_COMMAND).
     * Если ранее было указано, что конфигурация должна быть сохранена,
     * переход в состояние {@link ConfigureCommandState}.
     *
     * @param gatt    объект BluetoothGatt для взаимодействия с BLE-устройством
     * @param handler обработчик GATT, содержащий данные конфигурации и текущее состояние
     */
    @Override
    public void handle(BluetoothGatt gatt, BluetoothGattCallbackHandler handler) {
        handler.setCommand(SEVEN_COMMAND, SECOND_COMMAND);

        if (handler.isConfigurationSaved()) {
            handler.setCommandState(new ConfigureCommandState());
        }
    }
}
