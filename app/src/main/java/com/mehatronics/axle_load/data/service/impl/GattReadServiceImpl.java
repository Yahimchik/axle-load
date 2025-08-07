package com.mehatronics.axle_load.data.service.impl;

import static com.mehatronics.axle_load.constants.CommandsConstants.FIRST_COMMAND;
import static com.mehatronics.axle_load.constants.CommandsConstants.SEVEN_COMMAND;
import static com.mehatronics.axle_load.constants.CommandsConstants.ZERO_COMMAND_BINARY;
import static com.mehatronics.axle_load.constants.UuidConstants.READ_CHARACTERISTIC_DPS;
import static com.mehatronics.axle_load.constants.UuidConstants.USER_SERVICE_DPS;
import static com.mehatronics.axle_load.utils.ByteUtils.convertBytesToCalibrationTable;
import static com.mehatronics.axle_load.utils.ByteUtils.convertBytesToConfiguration;
import static com.mehatronics.axle_load.utils.ByteUtils.convertMultiplierToPortion;

import android.Manifest;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import androidx.annotation.RequiresPermission;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mehatronics.axle_load.data.mapper.GattDataMapper;
import com.mehatronics.axle_load.data.service.GattReadService;
import com.mehatronics.axle_load.domain.entities.CalibrationParseResult;
import com.mehatronics.axle_load.domain.entities.CalibrationTable;
import com.mehatronics.axle_load.domain.entities.SensorConfig;
import com.mehatronics.axle_load.domain.entities.device.DeviceDetails;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.inject.Inject;

/**
 * Реализация сервиса {@link GattReadService}, осуществляющего чтение данных с BLE-устройства
 * через GATT протокол. Поддерживает чтение конфигурации сенсора и таблицы калибровки.
 * Также управляет состоянием чтения и предоставляет LiveData для подписки на обновления.
 */
public class GattReadServiceImpl implements GattReadService {

    /**
     * Очередь для последовательного чтения GATT-характеристик.
     */
    private final Queue<BluetoothGattCharacteristic> characteristicsQueue = new LinkedList<>();

    /**
     * LiveData с данными об устройстве.
     */
    private final MutableLiveData<DeviceDetails> deviceDetailsLiveData = new MutableLiveData<>();

    /**
     * LiveData с конфигурацией сенсора.
     */
    private final MutableLiveData<SensorConfig> sensorConfigLiveData = new MutableLiveData<>();

    /**
     * LiveData, сигнализирующая об успешном сохранении конфигурации.
     */
    private final MutableLiveData<Boolean> configurationSavedLiveData = new MutableLiveData<>(false);

    /**
     * Маппер для преобразования BLE-данных в доменные сущности.
     */
    private final GattDataMapper gattDataMapper;

    /**
     * Хранение считанной калибровочной таблицы.
     */
    private final List<CalibrationTable> table = new ArrayList<>();

    /**
     * Список считанных "сырых" значений для анализа.
     */
    private final List<byte[]> values = new ArrayList<>();

    /**
     * Флаг: завершено ли чтение конфигурации.
     */
    private boolean isRieadingConfigComplete = false;

    /**
     * Флаг: завершено ли чтение калибровочной таблицы.
     */
    private boolean isReadingTableComplete = false;

    /**
     * Флаг: была ли сохранена конфигурация.
     */
    private boolean isConfigurationSaved = false;
    private boolean isPasswordReset = false;
    private boolean isPasswordSet = false;

    /**
     * Флаг: происходит ли чтение всех характеристик.
     */
    private boolean isReadingAll = false;

    /**
     * Флаг: была ли таблица сохранена.
     */
    private boolean isTableSaved = false;

    /**
     * Флаг подключения устройства.
     */
    private boolean isConnected = false;

    /**
     * Текущая страница калибровочной таблицы.
     */
    private int tablePage = 0;

    /**
     * MAC-адрес текущего устройства.
     */
    private String currentMac;

    /**
     * Конструктор с внедрением зависимого маппера.
     *
     * @param gattDataMapper преобразователь BLE-данных в доменные объекты
     */
    @Inject
    public GattReadServiceImpl(GattDataMapper gattDataMapper) {
        this.gattDataMapper = gattDataMapper;
    }

    /**
     * Возвращает текущую страницу таблицы калибровки.
     * <p>
     * Используется при пошаговом чтении таблицы калибровки, разбитой по страницам.
     *
     * @return номер текущей страницы
     */
    @Override
    public int getTablePage() {
        return tablePage;
    }

    /**
     * Устанавливает номер текущей страницы для чтения калибровочной таблицы.
     *
     * @param tablePage номер страницы (начиная с 0)
     */
    @Override
    public void setTablePage(int tablePage) {
        this.tablePage = tablePage;
    }

    /**
     * Инициирует последовательное чтение всех характеристик из GATT-сервисов.
     *
     * @param gatt активное соединение BluetoothGatt
     */
    @Override
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public void readAllCharacteristics(BluetoothGatt gatt) {
        characteristicsQueue.clear();
        for (var service : gatt.getServices()) {
            for (var characteristic : service.getCharacteristics()) {
                if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_READ) != 0) {
                    characteristicsQueue.add(characteristic);
                }
            }
        }
        isReadingAll = true;
        isRieadingConfigComplete = true;
        isReadingTableComplete = true;
        readNext(gatt);
    }

    /**
     * Обрабатывает результат чтения характеристики и формирует соответствующие модели данных.
     * При необходимости повторяет чтение (например, при подозрении на нулевой заряд батареи).
     *
     * @param gatt           соединение GATT
     * @param characteristic прочитанная характеристика
     */
    @Override
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public void handleRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        byte[] bytes = characteristic.getValue();

        if (values.size() < 11) {
            values.add(bytes);
        } else if (!Arrays.equals(values.get(values.size() - 1), bytes)) {
            values.set(values.size() - 1, bytes);
        }

        if (isReadingAll) {
            readNext(gatt);
            return;
        }

        if (isRieadingConfigComplete && isMatchingCommand(bytes, 0, SEVEN_COMMAND)
                && isMatchingCommand(bytes, 1, FIRST_COMMAND)) {
            currentMac = gatt.getDevice().getAddress();
            sensorConfigLiveData.postValue(convertBytesToConfiguration(gatt, bytes));
        }

        if (isReadingTableComplete && isMatchingCommand(bytes, 0, FIRST_COMMAND)) {
            CalibrationParseResult result = convertBytesToCalibrationTable(bytes, table, tablePage);
            tablePage = result.nextPage();
            if (result.tableCompleted()) {
                convertMultiplierToPortion(table);
                isReadingTableComplete = false;
            }
        }
        Log.d("MyTag", Arrays.toString(bytes));

        if (isConnected && values.size() > 8) {

            deviceDetailsLiveData.postValue(gattDataMapper.convertToDeviceDetails(gatt, values, table));
        }
    }

    /**
     * Повторно инициализирует чтение калибровочной таблицы.
     */
    @Override
    public void rereadCalibrationTable() {
        isReadingTableComplete = true;
        tablePage = 0;
        table.clear();
    }

    /**
     * Считывает характеристику после завершения записи в BLE.
     *
     * @param gatt активное соединение
     */
    @Override
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public void readNextAfterWrite(BluetoothGatt gatt) {
        var service = gatt.getService(USER_SERVICE_DPS);
        var readCharacteristic = service.getCharacteristic(READ_CHARACTERISTIC_DPS);
        if (readCharacteristic != null) {
            gatt.readCharacteristic(readCharacteristic);
        }
    }

    /**
     * Возвращает {@link LiveData} с текущими данными об устройстве.
     * <p>
     * Объект {@link DeviceDetails} содержит агрегированную информацию, такую как:
     * модель устройства, серийный номер, статус подключения и батареи, таблицу калибровки и пр.
     *
     * @return LiveData с данными об устройстве
     */
    @Override
    public LiveData<DeviceDetails> getDeviceDetailsLiveData() {
        return deviceDetailsLiveData;
    }

    /**
     * Принудительно устанавливает текущее состояние {@link DeviceDetails} в LiveData.
     * Может использоваться для ручного обновления интерфейса из ViewModel или callback'а.
     *
     * @param details объект с деталями устройства
     */
    @Override
    public void setDeviceDetailsLiveData(DeviceDetails details) {
        deviceDetailsLiveData.setValue(details);
    }

    /**
     * Возвращает {@link LiveData} с конфигурацией сенсора, полученной от BLE-устройства.
     * <p>
     * Конфигурация включает параметры сенсора: версия, тип, диапазон, батарея и прочее.
     *
     * @return LiveData с текущей конфигурацией сенсора
     */
    @Override
    public LiveData<SensorConfig> getSensorConfigureLiveData() {
        return sensorConfigLiveData;
    }

    /**
     * Очищает сохранённые детали устройства.
     * Устанавливает значение {@code null} в {@link #deviceDetailsLiveData}.
     */
    @Override
    public void clearDetails() {
        deviceDetailsLiveData.setValue(null);
    }

    /**
     * Устанавливает флаг, отражающий факт успешного сохранения конфигурации на устройстве.
     *
     * @param value {@code true}, если конфигурация была успешно сохранена
     */
    @Override
    public void setConfigurationSaved(boolean value) {
        isConfigurationSaved = value;
    }

    /**
     * Устанавливает флаг, отражающий факт успешного сохранения таблицы калибровки на устройстве.
     *
     * @param value {@code true}, если таблица была успешно сохранена
     */
    @Override
    public void setTableSaved(boolean value) {
        isTableSaved = value;
    }

    /**
     * Проверяет, была ли калибровочная таблица успешно сохранена на устройстве.
     *
     * @return {@code true}, если сохранение таблицы подтверждено
     */
    @Override
    public boolean isTableSaved() {
        return isTableSaved;
    }

    /**
     * Проверяет, была ли конфигурация сенсора успешно сохранена на устройстве.
     *
     * @return {@code true}, если сохранение конфигурации подтверждено
     */
    @Override
    public boolean isConfigurationSaved() {
        return isConfigurationSaved;
    }

    /**
     * Обновляет состояние подключения и очищает кэш с полученными данными.
     * <p>
     * Вызывается при отключении или новом подключении к устройству.
     *
     * @param isConnected {@code true}, если устройство подключено
     */
    @Override
    public void updateState(boolean isConnected) {
        this.isConnected = isConnected;
        values.clear();
        table.clear();
    }

    /**
     * Возвращает флаг текущего состояния полного чтения всех характеристик.
     *
     * @return {@code true}, если активно чтение всех характеристик
     */
    @Override
    public boolean isReadingAll() {
        return isReadingAll;
    }

    /**
     * Возвращает текущий MAC-адрес подключённого BLE-устройства.
     *
     * @return строка с MAC-адресом устройства
     */
    @Override
    public String getCurrentMac() {
        return currentMac;
    }

    /**
     * Устанавливает флаг сохранения конфигурации и публикует его в {@link LiveData}.
     *
     * @param value {@code true}, если конфигурация успешно сохранена
     */
    @Override
    public void setConfigurationSavedLive(boolean value) {
        isConfigurationSaved = value;
        configurationSavedLiveData.postValue(value);
    }

    /**
     * Возвращает {@link LiveData}, отражающую факт сохранения конфигурации на устройстве.
     * <p>
     * Это LiveData можно использовать во ViewModel/Fragment для наблюдения за статусом.
     *
     * @return LiveData<Boolean>, где {@code true} означает, что конфигурация сохранена
     */
    @Override
    public LiveData<Boolean> getConfigurationSavedLiveData() {
        return configurationSavedLiveData;
    }

    @Override
    public void resetPassword(boolean value) {
        isPasswordReset = value;
    }

    @Override
    public boolean isResetPassword() {
        return isPasswordReset;
    }

    @Override
    public void setPassword(boolean value) {
        isPasswordSet = value;
    }

    @Override
    public boolean isPasswordSet() {
        return isPasswordSet;
    }

    /**
     * Читает следующую характеристику из очереди.
     *
     * @param gatt активное GATT-соединение
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void readNext(BluetoothGatt gatt) {
        if (!characteristicsQueue.isEmpty()) {
            BluetoothGattCharacteristic next = characteristicsQueue.poll();
            if (next != null) {
                gatt.readCharacteristic(next);
            }
        } else {
            isReadingAll = false;
            Log.d("MyTag", "Finished reading all characteristics.");
        }
    }

    /**
     * Проверяет соответствие команды на определённой позиции.
     *
     * @param bytes   массив байт
     * @param index   индекс проверяемого байта
     * @param command ожидаемая команда
     * @return true, если команда совпадает
     */
    private boolean isMatchingCommand(byte[] bytes, int index, int command) {
        return (bytes[index] & ZERO_COMMAND_BINARY) == command;
    }
}