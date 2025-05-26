package com.mehatronics.axle_load.ble.parser;

import static com.mehatronics.axle_load.entities.enums.CharacteristicType.PRESSURE;
import static com.mehatronics.axle_load.entities.enums.CharacteristicType.WEIGHT;
import static com.mehatronics.axle_load.utils.ByteUtils.detectorToBytes;
import static com.mehatronics.axle_load.utils.ByteUtils.intToBytes;
import static com.mehatronics.axle_load.utils.ByteUtils.intToFourBytes;
import static com.mehatronics.axle_load.utils.ByteUtils.intToTwoBytes;
import static com.mehatronics.axle_load.utils.ByteUtils.multiplierToBytes;
import static com.mehatronics.axle_load.utils.ByteUtils.stringToBytes;
import static com.mehatronics.axle_load.utils.DataUtils.convertBytesToBattery;
import static com.mehatronics.axle_load.utils.DataUtils.convertBytesToDate;
import static com.mehatronics.axle_load.utils.DataUtils.convertBytesToString;
import static com.mehatronics.axle_load.utils.DataUtils.convertBytesToValue;
import static com.mehatronics.axle_load.utils.constants.ValueConstants.MAX_DETECTORS;
import static com.mehatronics.axle_load.utils.constants.ValueConstants.MAX_MULTIPLIER;

import com.mehatronics.axle_load.entities.CalibrationTable;
import com.mehatronics.axle_load.entities.DeviceDetails;
import com.mehatronics.axle_load.entities.SensorConfig;

import java.util.List;

import javax.inject.Inject;

/**
 * Класс для парсинга данных BLE GATT, поступающих от устройства.
 * Отвечает за преобразование байтовых массивов в объектные структуры приложения.
 */
public class GattDataParser {

    /**
     * Конструктор класса GattDataParser.
     * Может использоваться для внедрения зависимостей.
     */
    @Inject
    public GattDataParser() {
    }

    /**
     * Парсит список байтовых массивов и таблицу калибровочных данных
     * для создания объекта {@link DeviceDetails}.
     *
     * @param values Список байтовых массивов с данными устройства.
     *               Ожидается минимум 10 элементов.
     * @param table Таблица калибровочных данных.
     * @return Объект {@link DeviceDetails} с заполненными полями,
     *         либо null, если данных недостаточно.
     */
    public DeviceDetails parseDeviceDetails(List<byte[]> values, List<CalibrationTable> table) {
        if (values.size() < 9) return null;

        String deviceName = convertBytesToString(values.get(2));
        String dateManufacture = convertBytesToDate(values.get(3));
        String manufacturer = convertBytesToString(values.get(4));
        String modelType = convertBytesToString(values.get(5));
        String serialNumber = convertBytesToString(values.get(6));
        String firmwareVersion = convertBytesToString(values.get(7));
        String hardwareVersion = convertBytesToString(values.get(8));
        String batteryLevel = convertBytesToBattery(values.get(9));
        String weight = convertBytesToValue(values.get(10), WEIGHT);
        String pressure = convertBytesToValue(values.get(10), PRESSURE);

        return new DeviceDetails.Builder()
                .setDeviceName(deviceName)
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

    /**
     * Заполняет буфер байтов конфигурационными данными из объекта {@link SensorConfig}.
     *
     * @param sensorConfig Объект конфигурации датчика.
     * @param buffer Буфер байтов, который необходимо заполнить.
     *               Предполагается, что он имеет достаточный размер.
     */
    public void setConfigureSettings(SensorConfig sensorConfig, byte[] buffer) {

        intToFourBytes(buffer, sensorConfig.getConfigSystem(), 4);
        intToFourBytes(buffer, Float.floatToIntBits(sensorConfig.getMultiplier()), 8);
        intToFourBytes(buffer, Float.floatToIntBits(sensorConfig.getOffset()), 12);

        intToTwoBytes(buffer, sensorConfig.getBatteryMicrovoltsPerStep(), 16);
        intToTwoBytes(buffer, sensorConfig.getMessageDeliveryPeriod(), 18);
        intToTwoBytes(buffer, sensorConfig.getMeasurementPeriod(), 20);

        intToTwoBytes(buffer, sensorConfig.getDistanceBetweenAxlesOneTwoMm(), 22);
        intToTwoBytes(buffer, sensorConfig.getDistanceBetweenAxlesTwoThreeMm(), 24);
        intToTwoBytes(buffer, sensorConfig.getDistanceToWheel(), 26);

        intToBytes(buffer, sensorConfig.getConfigType(), 28);
        intToBytes(buffer, sensorConfig.getInstallationPoint(), 29);

        stringToBytes(buffer, sensorConfig.getStateNumber());
    }

    /**
     * Заполняет буфер байтов калибровочными данными из таблицы.
     * Используется пагинация — передается номер страницы таблицы.
     *
     * @param table Список объектов {@link CalibrationTable}.
     * @param buffer Буфер байтов для записи.
     * @param page Текущая страница (индекс блока) таблицы.
     * @return Следующая страница, либо -1, если достигнут конец таблицы.
     */
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
