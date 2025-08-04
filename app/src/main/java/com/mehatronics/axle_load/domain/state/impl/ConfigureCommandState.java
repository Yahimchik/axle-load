package com.mehatronics.axle_load.domain.state.impl;

import static com.mehatronics.axle_load.constants.CommandsConstants.FIRST_COMMAND;
import static com.mehatronics.axle_load.constants.CommandsConstants.SEVENTY_SEVEN_COMMAND;

import android.util.Log;

import com.mehatronics.axle_load.domain.handler.BluetoothGattCallbackHandler;
import com.mehatronics.axle_load.domain.state.CommandStateHandler;

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
     * @param handler обработчик GATT, содержащий текущую конфигурацию и состояние
     */
    @Override
    public void handle(BluetoothGattCallbackHandler handler) {
        handler.setCommand(SEVENTY_SEVEN_COMMAND, FIRST_COMMAND);
        handler.saveConfiguration();
        Log.d("MyTag", "Configuration sent");

        if (!handler.isConfigurationSaved()) {
            handler.setCommandState(new FinalCommandState());
        }
    }
}
