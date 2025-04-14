package com.mehatronics.axle_load.command.impl;

import android.bluetooth.BluetoothGatt;
import android.util.Log;

import com.mehatronics.axle_load.ble.handler.BluetoothGattCallbackHandler;
import com.mehatronics.axle_load.command.CommandStateHandler;

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
        handler.saveConfiguration();
        Log.d("MyTag", "Configuration sent");

        if (!handler.isConfigurationSaved()) {
            handler.setCommandState(new FinalCommandState());
        }
    }
}
