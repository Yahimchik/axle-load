package com.mehatronics.axle_load.state.impl;

import static com.mehatronics.axle_load.utils.constants.CommandsConstants.FIRST_COMMAND;
import static com.mehatronics.axle_load.utils.constants.CommandsConstants.SEVEN_COMMAND;

import android.bluetooth.BluetoothGatt;
import android.util.Log;

import com.mehatronics.axle_load.ble.handler.BluetoothGattCallbackHandler;
import com.mehatronics.axle_load.state.CommandStateHandler;

/**
 * Состояние "SECOND" в паттерне "Состояние" (State),
 * отправляющее вторую управляющую команду BLE-устройству.
 * <p>
 * В зависимости от необходимости сохранения конфигурации переходит
 * либо в {@link ConfigureCommandState}, либо в {@link FinalCommandState}.
 */
public class SecondCommandState implements CommandStateHandler {

    /**
     * Устанавливает вторую команду (SEVEN_COMMAND и FIRST_COMMAND)
     * и переводит обработчик в следующее состояние:
     * - {@link ConfigureCommandState}, если необходимо сохранить конфигурацию;
     * - {@link FinalCommandState}, если конфигурация не требуется.
     *
     * @param gatt    объект BluetoothGatt для взаимодействия с BLE-устройством
     * @param handler обработчик GATT, управляющий состоянием и данными BLE
     */
    @Override
    public void handle(BluetoothGatt gatt, BluetoothGattCallbackHandler handler) {
        handler.setCommand(SEVEN_COMMAND, FIRST_COMMAND);
        Log.d("MyTag", "Second command sent");
        if (handler.getTablePage() > 0) {
            handler.setCommandState(new FirstCommandState());
        }
        if (handler.isTableSaved()) {
            handler.setCommandState(new SaveTableCommand());
        } else if (handler.isConfigurationSaved()) {
            handler.setCommandState(new ConfigureCommandState());
        } else {
            handler.setCommandState(new FinalCommandState());
        }
    }
}
