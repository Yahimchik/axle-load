package com.mehatronics.axle_load.data.service;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import androidx.lifecycle.LiveData;

import com.mehatronics.axle_load.domain.entities.device.DeviceInfoToSave;
import com.mehatronics.axle_load.helper.SingleLiveEvent;
import com.mehatronics.axle_load.ui.adapter.listener.GattReadListener;
import com.mehatronics.axle_load.domain.entities.SensorConfig;
import com.mehatronics.axle_load.domain.entities.device.DeviceDetails;

/**
 * Сервис для чтения характеристик BLE-устройства и управления полученными данными.
 * Обеспечивает чтение конфигурации устройства, калибровочной таблицы и других параметров.
 */
public interface GattReadService {

    /**
     * Возвращает текущий номер страницы таблицы калибровки.
     *
     * @return номер страницы
     */
    int getTablePage();

    /**
     * Устанавливает номер страницы таблицы калибровки.
     *
     * @param tablePage номер страницы
     */
    void setTablePage(int tablePage);

    /**
     * Инициализирует чтение всех доступных характеристик устройства.
     * Заполняет очередь характеристик с флагом чтения и начинает последовательное чтение.
     *
     * @param gatt объект BluetoothGatt для доступа к BLE-устройству
     */
    void readAllCharacteristics(BluetoothGatt gatt);

    /**
     * Обрабатывает полученную характеристику после её чтения.
     * Определяет, какие данные были прочитаны, и обновляет соответствующее состояние.
     *
     * @param gatt           объект BluetoothGatt
     * @param characteristic характеристика, данные которой были прочитаны
     */
    void handleRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);

    /**
     * Перезапускает процесс чтения таблицы калибровки с первой страницы.
     * Используется для полного обновления таблицы.
     */
    void rereadCalibrationTable();

    /**
     * Запускает следующее чтение характеристики после завершения записи.
     * Полезно, когда требуется немедленно обновить значения после изменения.
     *
     * @param gatt объект BluetoothGatt
     */
    void readNextAfterWrite(BluetoothGatt gatt);

    /**
     * Возвращает LiveData с информацией о текущем подключенном устройстве.
     *
     * @return LiveData с {@link DeviceDetails}
     */
    LiveData<DeviceDetails> getDeviceDetailsLiveData();

    /**
     * Устанавливает вручную данные устройства.
     *
     * @param details объект {@link DeviceDetails}
     */
    void setDeviceDetailsLiveData(DeviceDetails details);

    /**
     * Возвращает LiveData с текущей конфигурацией сенсора.
     *
     * @return LiveData с {@link SensorConfig}
     */
    LiveData<SensorConfig> getSensorConfigureLiveData();

    /**
     * Очищает данные об устройстве (например, после отключения).
     */
    void clearDetails();

    /**
     * Устанавливает флаг сохранения конфигурации.
     *
     * @param value true — если конфигурация успешно сохранена
     */
    void setConfigurationSaved(boolean value);

    /**
     * Устанавливает флаг сохранения таблицы калибровки.
     *
     * @param value true — если таблица успешно сохранена
     */
    void setTableSaved(boolean value);

    /**
     * Возвращает флаг, отражающий, была ли таблица калибровки сохранена.
     *
     * @return true — если таблица сохранена
     */
    boolean isTableSaved();

    /**
     * Возвращает флаг, отражающий, была ли конфигурация устройства сохранена.
     *
     * @return true — если конфигурация сохранена
     */
    boolean isConfigurationSaved();

    /**
     * Обновляет внутреннее состояние подключения и очищает текущие данные.
     *
     * @param isConnected true — если устройство подключено
     */
    void updateState(boolean isConnected);

    /**
     * Проверяет, выполняется ли в данный момент полное чтение характеристик.
     *
     * @return true — если чтение всех характеристик активно
     */
    boolean isReadingAll();

    /**
     * Возвращает MAC-адрес текущего подключенного устройства.
     *
     * @return строка с MAC-адресом
     */
    String getCurrentMac();

    /**
     * Устанавливает флаг успешного сохранения конфигурации
     * и обновляет соответствующее LiveData.
     *
     * @param value true — если конфигурация успешно сохранена
     */
    void setConfigurationSavedLive(boolean value);

    /**
     * Возвращает LiveData, отслеживающее сохранение конфигурации.
     *
     * @return LiveData с флагом сохранения
     */
    LiveData<Boolean> getConfigurationSavedLiveData();

    void resetPassword(boolean value);

    boolean isResetPassword();

    void setPassword(boolean value);

    boolean isPasswordSet();

    void setListener(GattReadListener listener);

    boolean isSavedToBTCOMMini();

    void setSaveToBTCOMMini(boolean value);

    void setSaveToMiniLive(boolean value);

    SingleLiveEvent<Boolean> getSaveToMiniLive();

    void setDeviceInfoToSave(DeviceInfoToSave info);

    LiveData<DeviceInfoToSave> getDeviceInfoToSave();

    LiveData<String> getUiAxisList();
}