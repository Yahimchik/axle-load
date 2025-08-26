package com.mehatronics.axle_load.domain.state.impl;

import static com.mehatronics.axle_load.constants.CommandsConstants.SECOND_COMMAND;
import static com.mehatronics.axle_load.constants.CommandsConstants.SEVEN_COMMAND;
import static com.mehatronics.axle_load.domain.entities.enums.ConnectStatus.READ;
import static com.mehatronics.axle_load.domain.entities.enums.ConnectStatus.WHRITE;

import com.mehatronics.axle_load.domain.entities.enums.ConnectStatus;
import com.mehatronics.axle_load.domain.handler.BluetoothGattCallbackHandler;
import com.mehatronics.axle_load.domain.state.CommandStateHandler;

/**
 * Финальное состояние "FINAL" в паттерне "Состояние" (State),
 * устанавливающее последнюю команду для BLE-устройства.
 * <p>
 * Если требуется отправка конфигурации, состояние переходит к {@link ConfigureCommandState}.
 */
public class FinalCommandState implements CommandStateHandler {

    /**
     * Устанавливает финальную команду (SEVEN_COMMAND и SECOND_COMMAND).
     * Если ранее было указано, что конфигурация должна быть сохранена,
     * переход в состояние {@link ConfigureCommandState}.
     *
     * @param h обработчик GATT, содержащий данные конфигурации и текущее состояние
     */
    @Override
    public void handle(BluetoothGattCallbackHandler h) {
        h.setCommand(SEVEN_COMMAND, SECOND_COMMAND);

        if (h.isConfigurationSaved()) h.setCommandState(new ConfigureCommandState());
        else if (h.isTableSaved()) h.setCommandState(new SaveTableCommand());
        else if (h.isResetPassword()) h.setCommandState(new ResetPasswordCommandState());
        else if (h.isPasswordSet()) h.setCommandState(new SetPasswordCommandState());
        else if (h.isSavedToBTCOMMini() && whatStatus(h,WHRITE)) h.setCommandState(new SaveToBTCOMMiniCommandState());
        else if (whatStatus(h, READ)) h.setCommandState(new WhriteToBtComMiniBeforeReadingConfig());
        else if (h.isComplete()) h.setCommandState(new ReadFromBTCOMMiniCommandState());
    }

    private boolean whatStatus(BluetoothGattCallbackHandler h, ConnectStatus status) {
        return h.getRepository().getStatus().equals(status);
    }
}