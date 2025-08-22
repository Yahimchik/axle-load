package com.mehatronics.axle_load.data.mapper.impl;

import static com.mehatronics.axle_load.constants.ValueConstants.MAX_DETECTORS;
import static com.mehatronics.axle_load.constants.ValueConstants.MAX_MULTIPLIER;
import static com.mehatronics.axle_load.domain.entities.enums.CharacteristicType.BATTERY;
import static com.mehatronics.axle_load.domain.entities.enums.CharacteristicType.PRESSURE;
import static com.mehatronics.axle_load.domain.entities.enums.CharacteristicType.WEIGHT;
import static com.mehatronics.axle_load.utils.ByteUtils.composeChassisNumber;
import static com.mehatronics.axle_load.utils.ByteUtils.composeSensorNumber;
import static com.mehatronics.axle_load.utils.ByteUtils.detectorToBytes;
import static com.mehatronics.axle_load.utils.ByteUtils.intToBytes;
import static com.mehatronics.axle_load.utils.ByteUtils.intToFourBytes;
import static com.mehatronics.axle_load.utils.ByteUtils.intToTwoBytes;
import static com.mehatronics.axle_load.utils.ByteUtils.multiplierToBytes;
import static com.mehatronics.axle_load.utils.ByteUtils.stringToBytes;
import static com.mehatronics.axle_load.utils.ByteUtils.stringToBytesCOM;
import static com.mehatronics.axle_load.utils.DataUtils.convertBytesToString;
import static com.mehatronics.axle_load.utils.DataUtils.convertBytesToValue;

import android.bluetooth.BluetoothGatt;

import com.mehatronics.axle_load.data.mapper.DateFormatMapper;
import com.mehatronics.axle_load.data.mapper.GattDataMapper;
import com.mehatronics.axle_load.data.repository.DeviceTypeRepository;
import com.mehatronics.axle_load.domain.entities.CalibrationTable;
import com.mehatronics.axle_load.domain.entities.SensorConfig;
import com.mehatronics.axle_load.domain.entities.device.BTCOMMiniDetails;
import com.mehatronics.axle_load.domain.entities.device.DeviceDetails;
import com.mehatronics.axle_load.domain.entities.device.DeviceInfoToSave;
import com.mehatronics.axle_load.domain.entities.enums.DeviceType;

import java.util.List;

import javax.inject.Inject;

/**
 * Класс для парсинга данных BLE GATT, поступающих от устройства.
 * Отвечает за преобразование байтовых массивов в объектные структуры приложения.
 */
public class GattDataMapperImpl implements GattDataMapper {

    private final DateFormatMapper dateFormatMapper;
    private final DeviceTypeRepository repository;

    @Inject
    public GattDataMapperImpl(DateFormatMapper dateFormatMapper, DeviceTypeRepository repository) {
        this.dateFormatMapper = dateFormatMapper;
        this.repository = repository;
    }

    /**
     * Парсит список байтовых массивов и таблицу калибровочных данных
     * для создания объекта {@link DeviceDetails}.
     *
     * @param values Список байтовых массивов с данными устройства.
     *               Ожидается минимум 10 элементов.
     * @param table  Таблица калибровочных данных.
     * @return Объект {@link DeviceDetails} с заполненными полями,
     * либо null, если данных недостаточно.
     */
    @Override
    public DeviceDetails convertToDeviceDetails(BluetoothGatt gatt, List<byte[]> values, List<CalibrationTable> table) {
        if (values.size() < 9) return null;
        String deviceName;
        if (repository.getCurrDeviceType().equals(DeviceType.BT_COM_MINI)) {
            deviceName = convertBytesToString(values.get(0));
        } else {
            deviceName = convertBytesToString(values.get(2));
        }

        String dateManufacture = dateFormatMapper.convertToDate(values);
        String manufacturer = convertBytesToString(values.get(4));
        String modelType = convertBytesToString(values.get(5));
        String serialNumber = convertBytesToString(values.get(6));
        String firmwareVersion = convertBytesToString(values.get(7));
        String hardwareVersion = convertBytesToString(values.get(8));
        String batteryLevel = convertBytesToValue(values.get(10), BATTERY);
        String weight = convertBytesToValue(values.get(10), WEIGHT);
        String pressure = convertBytesToValue(values.get(10), PRESSURE);

        return new DeviceDetails.Builder()
                .setDeviceName(deviceName)
                .setDeviceMac(gatt.getDevice().getAddress())
                .setDateManufacturer(dateManufacture)
                .setManufacturer(manufacturer)
                .setModelType(modelType)
                .setSerialNumber(serialNumber)
                .setFirmwareVersion(firmwareVersion)
                .setHardWareVersion(hardwareVersion)
                .setBatteryLevel(batteryLevel)
                .setWeight(weight)
                .setPressure(pressure)
                .setTable(table).build();
    }

    @Override
    public BTCOMMiniDetails convertToBTCOMMiniDetails(BluetoothGatt gatt, List<byte[]> values, List<CalibrationTable> table) {
        String deviceName = convertBytesToString(values.get(0));
        String dateManufacture = dateFormatMapper.convertToDate(values);
        String firmwareVersion = convertBytesToString(values.get(7));
        String hardwareVersion = convertBytesToString(values.get(8));

        return new BTCOMMiniDetails.Builder()
                .deviceName(deviceName)
                .dateManufacture(dateManufacture)
                .firmwareVersion(firmwareVersion)
                .hardwareVersion(hardwareVersion)
                .build();
    }

    /**
     * Заполняет буфер байтов конфигурационными данными из объекта {@link SensorConfig}.
     *
     * @param conf Объект конфигурации датчика.
     * @param buffer       Буфер байтов, который необходимо заполнить.
     *                     Предполагается, что он имеет достаточный размер.
     */
    @Override
    public void setConfigureSettings(SensorConfig conf, byte[] buffer) {

        intToFourBytes(buffer, conf.getConfigSystem(), 4);
        intToFourBytes(buffer, Float.floatToIntBits(conf.getMultiplier()), 8);
        intToFourBytes(buffer, Float.floatToIntBits(conf.getOffset()), 12);

        intToTwoBytes(buffer, conf.getBatteryMicrovoltsPerStep(), 16);
        intToTwoBytes(buffer, conf.getMessageDeliveryPeriod(), 18);
        intToTwoBytes(buffer, conf.getMeasurementPeriod(), 20);

        intToTwoBytes(buffer, conf.getDistanceBetweenAxlesOneTwoMm(), 22);
        intToTwoBytes(buffer, conf.getDistanceBetweenAxlesTwoThreeMm(), 24);
        intToTwoBytes(buffer, conf.getDistanceToWheel(), 26);

        intToBytes(buffer, composeSensorNumber(conf), 29);
        intToBytes(buffer, composeChassisNumber(conf), 28);

        stringToBytes(buffer, conf.getStateNumber());
    }

    @Override
    public void setBTCOMMiniSettings(DeviceInfoToSave save, byte[] buffer) {
        intToFourBytes(buffer, save.getPassword(), 4);
        buffer[8] = (byte) save.getType();
        stringToBytesCOM(buffer, save.getCarNumberFirst(), 9, 19);
        stringToBytesCOM(buffer, save.getCarNumberSecond(), 19, 29);
    }

    /**
     * Заполняет буфер байтов калибровочными данными из таблицы.
     * Используется пагинация — передается номер страницы таблицы.
     *
     * @param table  Список объектов {@link CalibrationTable}.
     * @param buffer Буфер байтов для записи.
     * @param page   Текущая страница (индекс блока) таблицы.
     * @return Следующая страница, либо -1, если достигнут конец таблицы.
     */
    @Override
    public int setCalibrationTable(List<CalibrationTable> table, byte[] buffer, int page) {
        for (int i = 0; i < MAX_DETECTORS; ++i) {
            int intBits = table.get(page * 9 + i).getDetector();
            detectorToBytes(buffer, i, intBits);

            intBits = Float.floatToIntBits(table.get(page * 9 + i).getMultiplier());
            multiplierToBytes(buffer, i, intBits);

            if (table.get(page * 9 + i).getMultiplier() == MAX_MULTIPLIER) {
                page = -1;
                return page;
            }
        }
        return page < 1 ? page + 1 : -1;
    }
}