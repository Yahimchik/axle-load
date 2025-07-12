package com.mehatronics.axle_load.data.service;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import androidx.lifecycle.LiveData;

import com.mehatronics.axle_load.domain.entities.device.DeviceDetails;
import com.mehatronics.axle_load.domain.entities.SensorConfig;

public interface GattReadService {
    int getTablePage();

    /**
     * Установить номер страницы таблицы калибровки.
     *
     * @param tablePage номер страницы
     */
    void setTablePage(int tablePage);

    /**
     * Инициализирует чтение всех доступных характеристик устройства.
     * Заполняет очередь характеристик, у которых есть право на чтение,
     * устанавливает соответствующие флаги и начинает чтение.
     *
     * @param gatt объект BluetoothGatt для чтения характеристик
     */
    void readAllCharacteristics(BluetoothGatt gatt);

    /**
     * Обрабатывает данные, полученные после чтения характеристики.
     * Анализирует команды, обновляет состояние чтения и публикует данные
     * в LiveData.
     *
     * @param gatt           объект BluetoothGatt
     * @param characteristic прочитанная характеристика
     */
    void handleRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);

    /**
     * Перезапускает чтение таблицы калибровки:
     * очищает текущие данные и сбрасывает номер страницы.
     */
    void rereadCalibrationTable();

    /**
     * Запускает чтение характеристики после записи для обновления данных.
     *
     * @param gatt объект BluetoothGatt
     */
    void readNextAfterWrite(BluetoothGatt gatt);

    /**
     * Возвращает LiveData с деталями устройства.
     *
     * @return LiveData с DeviceDetails
     */
    LiveData<DeviceDetails> getDeviceDetailsLiveData();

    /**
     * Устанавливает данные деталей устройства вручную.
     *
     * @param details данные DeviceDetails
     */
    void setDeviceDetailsLiveData(DeviceDetails details);

    /**
     * Возвращает LiveData с конфигурацией сенсора.
     *
     * @return LiveData с SensorConfig
     */
    LiveData<SensorConfig> getSensorConfigureLiveData();

    /**
     * Очищает данные деталей устройства.
     */
    void clearDetails();

    /**
     * Устанавливает флаг сохранения конфигурации.
     *
     * @param value состояние сохранения конфигурации
     */
    void setConfigurationSaved(boolean value);

    /**
     * Устанавливает флаг сохранения таблицы калибровки.
     *
     * @param value состояние сохранения таблицы
     */
    void setTableSaved(boolean value);

    /**
     * Проверяет, сохранена ли таблица калибровки.
     *
     * @return true, если таблица сохранена
     */
    boolean isTableSaved();

    /**
     * Проверяет, сохранена ли конфигурация сенсора.
     *
     * @return true, если конфигурация сохранена
     */
    boolean isConfigurationSaved();

    /**
     * Обновляет состояние подключения устройства и очищает накопленные данные.
     *
     * @param isConnected true, если устройство подключено
     */
    void updateState(boolean isConnected);

    /**
     * Проверяет, идёт
     * ли в данный момент чтение всех характеристик.
     *
     * @return true, если чтение всех характеристик активно
     */
    boolean isReadingAll();

    String getCurrentMac();
}
