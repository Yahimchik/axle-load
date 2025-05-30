package com.mehatronics.axle_load.state.impl;

import static com.mehatronics.axle_load.utils.constants.CommandsConstants.FIRST_COMMAND;
import static com.mehatronics.axle_load.utils.constants.CommandsConstants.SEVEN_COMMAND;

import android.bluetooth.BluetoothGatt;
import android.util.Log;

import com.mehatronics.axle_load.handler.BluetoothGattCallbackHandler;
import com.mehatronics.axle_load.state.CommandStateHandler;

/**
 * Состояние "SECOND" в паттерне "Состояние" (State), отвечающее за отправку второй управляющей команды BLE-устройству.
 * <p>
 * При выполнении устанавливает команды SEVEN_COMMAND и FIRST_COMMAND в обработчике,
 * затем переключает состояние обработчика в зависимости от текущих условий:
 * <ul>
 *   <li>Если количество страниц таблицы > 0 и таблица сохранена — переходит в состояние {@link SaveTableCommand}.</li>
 *   <li>Если конфигурация сохранена — переходит в состояние {@link ConfigureCommandState}.</li>
 *   <li>В противном случае — переходит в состояние {@link FinalCommandState}.</li>
 * </ul>
 * Также предусмотрена установка в {@link FirstCommandState} при определённых условиях (в коде есть проверка handler.getTablePage() > 0).
 */
public class SecondCommandState implements CommandStateHandler {

    /**
     * Обрабатывает отправку второй команды BLE-устройству и переключение состояния.
     *
     * @param gatt    объект {@link BluetoothGatt} для взаимодействия с BLE-устройством.
     * @param handler обработчик {@link BluetoothGattCallbackHandler}, управляющий состоянием и передачей данных.
     */
    @Override
    public void handle(BluetoothGatt gatt, BluetoothGattCallbackHandler handler) {
        handler.setCommand(SEVEN_COMMAND, FIRST_COMMAND);
        Log.d("MyTag", "Second command sent");

        // Если есть страницы таблицы, переходим в FirstCommandState
        if (handler.getTablePage() > 0) {
            handler.setCommandState(new FirstCommandState());
        }
        // Если таблица сохранена и есть страницы - переходим к сохранению таблицы
        if (handler.isTableSaved() && handler.getTablePage() > 0) {
            handler.setCommandState(new SaveTableCommand());
        }
        // Если конфигурация сохранена - переходим к состоянию конфигурации
        else if (handler.isConfigurationSaved()) {
            handler.setCommandState(new ConfigureCommandState());
        }
        // В противном случае - завершаем процесс командой FinalCommandState
        else {
            handler.setCommandState(new FinalCommandState());
        }
    }
}
