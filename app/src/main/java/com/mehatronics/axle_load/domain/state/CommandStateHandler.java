package com.mehatronics.axle_load.domain.state;

import com.mehatronics.axle_load.domain.handler.BluetoothGattCallbackHandler;

/**
 * Интерфейс состояния команды (паттерн State) для обработки логики передачи команд
 * BLE-устройству в зависимости от текущего состояния.
 * <p>
 * Каждое состояние реализует метод {@code handle}, который выполняет определённое действие
 * (например, отправку команды или конфигурации) и устанавливает следующее состояние при необходимости.
 * <p>
 * Используется в {@link BluetoothGattCallbackHandler} для инкапсуляции поведения,
 * связанного с передачей команд BLE-устройству.
 */
public interface CommandStateHandler {
    /**
     * Обрабатывает текущее состояние команды и выполняет соответствующее действие
     * с использованием BluetoothGatt и обработчика GATT-событий.
     *
     * @param handler ссылка на обработчик GATT, в котором находится текущее состояние
     */
    void handle(BluetoothGattCallbackHandler handler);
}
