package com.mehatronics.axle_load.domain.state.impl;

import static com.mehatronics.axle_load.constants.CommandsConstants.FIRST_COMMAND;
import static com.mehatronics.axle_load.constants.CommandsConstants.SEVEN_COMMAND;

import android.util.Log;

import com.mehatronics.axle_load.domain.handler.BluetoothGattCallbackHandler;
import com.mehatronics.axle_load.domain.state.CommandStateHandler;

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
     * @param h обработчик {@link BluetoothGattCallbackHandler}, управляющий состоянием и передачей данных.
     */
    @Override
    public void handle(BluetoothGattCallbackHandler h) {
        h.setCommand(SEVEN_COMMAND, FIRST_COMMAND);
        Log.d("MyTag", "Second command sent");

        if (h.getTablePage() > 0) h.setCommandState(new FirstCommandState());
        if (h.isTableSaved() && h.getTablePage() > 0) h.setCommandState(new SaveTableCommand());
        else if (h.isConfigurationSaved()) h.setCommandState(new ConfigureCommandState());
        else if (h.isSavedToBTCOMMini()) h.setCommandState(new SaveToBTCOMMiniCommandState());
        else h.setCommandState(new FinalCommandState());
    }
}
