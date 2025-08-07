package com.mehatronics.axle_load.data.service.impl;

import static com.mehatronics.axle_load.constants.UuidConstants.USER_SERVICE_DPS;
import static com.mehatronics.axle_load.constants.UuidConstants.WRITE_CHARACTERISTIC_DPS;

import android.Manifest;
import android.bluetooth.BluetoothGatt;
import android.util.Log;

import androidx.annotation.RequiresPermission;

import com.mehatronics.axle_load.data.service.GattWriteService;
import com.mehatronics.axle_load.domain.strategy.CommandStrategy;

import java.util.Arrays;
import java.util.Map;

import javax.inject.Inject;

/**
 * Реализация {@link GattWriteService} для записи данных в BLE-устройство.
 * <p>
 * Использует стратегию команд {@link CommandStrategy}, чтобы заполнить буфер
 * и записать его в характеристику WRITE_CHARACTERISTIC_DPS.
 */
public class GattWriteServiceImpl implements GattWriteService {

    private final Map<String, CommandStrategy> commandStrategies;
    private final byte[] buffer = new byte[68];

    /**
     * Внедрение зависимостей через конструктор.
     *
     * @param commandStrategies карта стратегий по ключу "C1-C2", где C1 и C2 — команды
     */
    @Inject
    public GattWriteServiceImpl(Map<String, CommandStrategy> commandStrategies) {
        this.commandStrategies = commandStrategies;
    }

    /**
     * Устанавливает команду в буфер и применяет соответствующую стратегию.
     * <p>
     * Буфер заполняется начальными байтами команды C1 и C2.
     * Если для команды найдена стратегия — она дополняет буфер необходимыми данными.
     *
     * @param c1 первый байт команды
     * @param c2 второй байт команды
     */
    @Override
    public void setCommand(int c1, int c2) {
        buffer[0] = (byte) c1;
        buffer[1] = (byte) c2;
        buffer[2] = 0;
        buffer[3] = 0;

        CommandStrategy strategy = commandStrategies.get(key(c1, c2));
        if (strategy != null) {
            strategy.fillBuffer(buffer);
        }
    }

    /**
     * Возвращает текущий буфер с подготовленными данными для отправки.
     *
     * @return массив байт длиной 68
     */
    @Override
    public byte[] getBuffer() {
        return buffer;
    }

    /**
     * Очищает буфер, заполняя его нулями.
     */
    @Override
    public void clearBuffer() {
        Arrays.fill(buffer, (byte) 0);
    }

    /**
     * Пишет содержимое буфера в WRITE-характеристику BLE-устройства.
     * <p>
     * Поиск производится по UUID сервиса {@link com.mehatronics.axle_load.constants.UuidConstants#USER_SERVICE_DPS} и
     * характеристики {@link com.mehatronics.axle_load.constants.UuidConstants#WRITE_CHARACTERISTIC_DPS}.
     * <p>
     * Метод требует разрешение {@link Manifest.permission#BLUETOOTH_CONNECT}.
     *
     * @param gatt экземпляр {@link BluetoothGatt}, связанный с устройством
     */
    @Override
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public void write(BluetoothGatt gatt) {
        var service = gatt.getService(USER_SERVICE_DPS);
        if (service == null) return;

        var characteristic = service.getCharacteristic(WRITE_CHARACTERISTIC_DPS);
        if (characteristic == null) return;

        characteristic.setValue(buffer);
        gatt.writeCharacteristic(characteristic);
    }

    /**
     * Создаёт ключ по C1 и C2 для поиска стратегии.
     *
     * @param c1 первый байт команды
     * @param c2 второй байт команды
     * @return строка в формате "C1-C2"
     */
    private String key(int c1, int c2) {
        return c1 + "-" + c2;
    }
}
