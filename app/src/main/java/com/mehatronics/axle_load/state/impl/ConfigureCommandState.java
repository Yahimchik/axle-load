package com.mehatronics.axle_load.state.impl;

import static com.mehatronics.axle_load.utils.constants.CommandsConstants.FIRST_COMMAND;
import static com.mehatronics.axle_load.utils.constants.CommandsConstants.SEVENTY_SEVEN_COMMAND;

import android.bluetooth.BluetoothGatt;
import android.util.Log;

import com.mehatronics.axle_load.handler.BluetoothGattCallbackHandler;
import com.mehatronics.axle_load.state.CommandStateHandler;

/**
 * Состояние "CONFIGURE" для передачи конфигурационных данных BLE-устройству.
 * <p>
 * В рамках паттерна "Состояние" (State) реализует поведение, связанное с отправкой
 * текущей конфигурации, полученной из {@link BluetoothGattCallbackHandler}, в устройство.
 * <p>
 * После отправки конфигурации состояние может измениться на {@link FinalCommandState},
 * если конфигурация была успешно сохранена.
 */
public class ConfigureCommandState implements CommandStateHandler {

    /**
     * Выполняет отправку конфигурации в BLE-устройство.
     * Если конфигурация успешно отправлена, переходит к финальному состоянию.
     *
     * @param gatt    объект BluetoothGatt для взаимодействия с устройством
     * @param handler обработчик GATT, содержащий текущую конфигурацию и состояние
     */
    @Override
    public void handle(BluetoothGatt gatt, BluetoothGattCallbackHandler handler) {
        handler.setCommand(SEVENTY_SEVEN_COMMAND, FIRST_COMMAND);
        handler.saveConfiguration();
        Log.d("MyTag", "Configuration sent");

        if (!handler.isConfigurationSaved()) {
            handler.setCommandState(new FinalCommandState());
        }
    }
}
