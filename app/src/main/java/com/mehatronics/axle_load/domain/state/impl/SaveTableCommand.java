package com.mehatronics.axle_load.domain.state.impl;

import static com.mehatronics.axle_load.constants.CommandsConstants.SEVENTY_ONE_COMMAND;

import android.bluetooth.BluetoothGatt;

import com.mehatronics.axle_load.domain.handler.BluetoothGattCallbackHandler;
import com.mehatronics.axle_load.domain.state.CommandStateHandler;

/**
 * Состояние, отвечающее за отправку команды сохранения калибровочной таблицы
 * на BLE-устройство.
 * <p>
 * Отправляет команду SEVENTY_ONE_COMMAND с номером текущей страницы таблицы,
 * инициирует процесс сохранения таблицы на устройстве через {@link BluetoothGattCallbackHandler}.
 * Если таблица не сохранена, переходит в состояние {@link CommandAfterAuth} для повторной авторизации или корректировки.
 */
public class SaveTableCommand implements CommandStateHandler {

    /**
     * Выполняет отправку команды сохранения таблицы и переключение состояния.
     *
     * @param gatt    объект BluetoothGatt для связи с BLE-устройством
     * @param handler обработчик BLE-команд и состояния
     */
    @Override
    public void handle(BluetoothGatt gatt, BluetoothGattCallbackHandler handler) {
        handler.setCommand(SEVENTY_ONE_COMMAND, handler.getTablePage());
        handler.saveTableToSensor();
        if (!handler.isTableSaved()) {
            handler.setCommandState(new CommandAfterAuth());
        }
    }
}
