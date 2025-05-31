package com.mehatronics.axle_load.domain.state;

import android.bluetooth.BluetoothGatt;

import com.mehatronics.axle_load.domain.handler.BluetoothGattCallbackHandler;

/**
 * Интерфейс состояния команды (паттерн State) для обработки логики передачи команд
 * BLE-устройству в зависимости от текущего состояния.
 *
 * Каждое состояние реализует метод {@code handle}, который выполняет определённое действие
 * (например, отправку команды или конфигурации) и устанавливает следующее состояние при необходимости.
 *
 * Используется в {@link BluetoothGattCallbackHandler} для инкапсуляции поведения,
 * связанного с передачей команд BLE-устройству.
 */
public interface CommandStateHandler {
    /**
     * Обрабатывает текущее состояние команды и выполняет соответствующее действие
     * с использованием BluetoothGatt и обработчика GATT-событий.
     *
     * @param gatt    объект BluetoothGatt для взаимодействия с BLE-устройством
     * @param handler ссылка на обработчик GATT, в котором находится текущее состояние
     */
    void handle(BluetoothGatt gatt, BluetoothGattCallbackHandler handler);
}
