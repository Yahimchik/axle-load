package com.mehatronics.axle_load.data.service.impl;

import static com.mehatronics.axle_load.constants.CommandsConstants.FIRST_COMMAND;
import static com.mehatronics.axle_load.constants.CommandsConstants.SEVEN_COMMAND;
import static com.mehatronics.axle_load.constants.CommandsConstants.ZERO_COMMAND_BINARY;
import static com.mehatronics.axle_load.constants.UuidConstants.READ_CHARACTERISTIC_DPS;
import static com.mehatronics.axle_load.constants.UuidConstants.USER_SERVICE_DPS;
import static com.mehatronics.axle_load.utils.ByteUtils.convertBytesToCalibrationTable;
import static com.mehatronics.axle_load.utils.ByteUtils.convertBytesToConfiguration;
import static com.mehatronics.axle_load.utils.ByteUtils.convertMultiplierToPortion;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

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
 * Класс, обрабатывающий чтение данных из BLE-устройства по протоколу GATT.
 * <p>
 * Управляет последовательным чтением характеристик устройства, хранит и обрабатывает
 * полученные данные о деталях устройства, конфигурации сенсора и таблице калибровки.
 * Обеспечивает хранение состояния процесса чтения и публикацию обновлений через LiveData.
 * </p>
 */
public class GattReadServiceImpl implements GattReadService {
    /**
     * Очередь характеристик для последовательного чтения.
     */
    private final Queue<BluetoothGattCharacteristic> characteristicsQueue = new LinkedList<>();
    /**
     * LiveData с данными деталей устройства, обновляется после успешного парсинга.
     */
    private final MutableLiveData<DeviceDetails> deviceDetailsLiveData = new MutableLiveData<>();
    /**
     * LiveData с конфигурацией сенсора, обновляется после получения данных конфигурации.
     */
    private final MutableLiveData<SensorConfig> sensorConfigLiveData = new MutableLiveData<>();
    /**
     * Парсер данных GATT.
     */
    private final GattDataMapper gattDataMapper;
    /**
     * Список калибровочных таблиц, собираемых по страницам.
     */
    private final List<CalibrationTable> table = new ArrayList<>();
    /**
     * Список принятых сырых значений байт для последующего анализа.
     */
    private final List<byte[]> values = new ArrayList<>();
    /**
     * Флаг, указывающий, что чтение конфигурации сенсора завершено.
     */
    private boolean isRieadingConfigComplete = false;
    /**
     * Флаг, указывающий, что чтение таблицы калибровки завершено.
     */
    private boolean isReadingTableComplete = false;
    /**
     * Флаг, что конфигурация сохранена.
     */
    private boolean isConfigurationSaved = false;
    /**
     * Флаг, указывающий, что происходит чтение всех характеристик устройства.
     */
    private boolean isReadingAll = false;
    /**
     * Флаг, что таблица калибровки сохранена.
     */
    private boolean isTableSaved = false;
    /**
     * Флаг, что устройство подключено.
     */
    private boolean isConnected = false;
    /**
     * Номер текущей страницы таблицы калибровки.
     */
    private int tablePage = 0;

    /**
     * Конструктор с внедрением зависимостей.
     */
    @Inject
    public GattReadServiceImpl(GattDataMapper gattDataMapper) {
        this.gattDataMapper = gattDataMapper;
    }

    private String currentMac;

    public String getCurrentMac() {
        return currentMac;
    }

    /**
     * Получить текущий номер страницы таблицы калибровки.
     *
     * @return номер страницы таблицы
     */
    public int getTablePage() {
        return tablePage;
    }

    /**
     * Установить номер страницы таблицы калибровки.
     *
     * @param tablePage номер страницы
     */
    public void setTablePage(int tablePage) {
        this.tablePage = tablePage;
    }

    /**
     * Инициализирует чтение всех доступных характеристик устройства.
     * Заполняет очередь характеристик, у которых есть право на чтение,
     * устанавливает соответствующие флаги и начинает чтение.
     *
     * @param gatt объект BluetoothGatt для чтения характеристик
     */
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
     * Обрабатывает данные, полученные после чтения характеристики.
     * Анализирует команды, обновляет состояние чтения и публикует данные
     * в LiveData.
     *
     * @param gatt           объект BluetoothGatt
     * @param characteristic прочитанная характеристика
     */
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
            isRieadingConfigComplete = false;
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

        if (isConnected && values.size() > 8) {
            deviceDetailsLiveData.postValue(gattDataMapper.convertToDeviceDetails(gatt, values, table));
        }
    }

    /**
     * Перезапускает чтение таблицы калибровки:
     * очищает текущие данные и сбрасывает номер страницы.
     */
    @Override
    public void rereadCalibrationTable() {
        isReadingTableComplete = true;
        tablePage = 0;
        table.clear();
    }

    /**
     * Запускает чтение характеристики после записи для обновления данных.
     *
     * @param gatt объект BluetoothGatt
     */
    @Override
    public void readNextAfterWrite(BluetoothGatt gatt) {
        var service = gatt.getService(USER_SERVICE_DPS);
        var readCharacteristic = service.getCharacteristic(READ_CHARACTERISTIC_DPS);
        if (readCharacteristic != null) {
            try {
                gatt.readCharacteristic(readCharacteristic);
            } catch (SecurityException e) {
                Log.d("MyTag", "Security exception: " + e.getMessage());
            }
        }
    }

    /**
     * Возвращает LiveData с деталями устройства.
     *
     * @return LiveData с DeviceDetails
     */
    @Override
    public LiveData<DeviceDetails> getDeviceDetailsLiveData() {
        return deviceDetailsLiveData;
    }

    /**
     * Устанавливает данные деталей устройства вручную.
     *
     * @param details данные DeviceDetails
     */
    @Override
    public void setDeviceDetailsLiveData(DeviceDetails details) {
        deviceDetailsLiveData.setValue(details);
    }

    /**
     * Возвращает LiveData с конфигурацией сенсора.
     *
     * @return LiveData с SensorConfig
     */
    @Override
    public LiveData<SensorConfig> getSensorConfigureLiveData() {
        return sensorConfigLiveData;
    }

    /**
     * Очищает данные деталей устройства.
     */
    @Override
    public void clearDetails() {
        deviceDetailsLiveData.setValue(null);
    }

    /**
     * Устанавливает флаг сохранения конфигурации.
     *
     * @param value состояние сохранения конфигурации
     */
    @Override
    public void setConfigurationSaved(boolean value) {
        isConfigurationSaved = value;
    }

    /**
     * Устанавливает флаг сохранения таблицы калибровки.
     *
     * @param value состояние сохранения таблицы
     */
    @Override
    public void setTableSaved(boolean value) {
        isTableSaved = value;
    }

    /**
     * Проверяет, сохранена ли таблица калибровки.
     *
     * @return true, если таблица сохранена
     */
    @Override
    public boolean isTableSaved() {
        return isTableSaved;
    }

    /**
     * Проверяет, сохранена ли конфигурация сенсора.
     *
     * @return true, если конфигурация сохранена
     */
    @Override
    public boolean isConfigurationSaved() {
        return isConfigurationSaved;
    }

    /**
     * Обновляет состояние подключения устройства и очищает накопленные данные.
     *
     * @param isConnected true, если устройство подключено
     */
    @Override
    public void updateState(boolean isConnected) {
        this.isConnected = isConnected;
        values.clear();
        table.clear();
    }

    /**
     * Проверяет, идёт
     * ли в данный момент чтение всех характеристик.
     *
     * @return true, если чтение всех характеристик активно
     */
    @Override
    public boolean isReadingAll() {
        return isReadingAll;
    }

    /**
     * Считывает следующую характеристику из очереди.
     *
     * @param gatt объект BluetoothGatt
     */
    private void readNext(BluetoothGatt gatt) {
        if (!characteristicsQueue.isEmpty()) {
            BluetoothGattCharacteristic next = characteristicsQueue.poll();
            if (next != null) {
                try {
                    gatt.readCharacteristic(next);
                } catch (SecurityException e) {
                    Log.d("MyTag", "Security exception: " + e.getMessage());
                }
            }
        } else {
            isReadingAll = false;
            Log.d("MyTag", "Finished reading all characteristics.");
        }
    }

    /**
     * Проверяет, соответствует ли команда из массива байт заданному значению.
     *
     * @param bytes   массив байт
     * @param index   индекс байта для проверки
     * @param command команда для сравнения
     * @return true, если команда совпадает
     */
    private boolean isMatchingCommand(byte[] bytes, int index, int command) {
        return (bytes[index] & ZERO_COMMAND_BINARY) == command;
    }
}