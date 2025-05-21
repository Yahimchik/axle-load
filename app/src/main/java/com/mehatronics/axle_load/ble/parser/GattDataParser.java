package com.mehatronics.axle_load.ble.parser;

import static com.mehatronics.axle_load.entities.enums.CharacteristicType.PRESSURE;
import static com.mehatronics.axle_load.entities.enums.CharacteristicType.WEIGHT;
import static com.mehatronics.axle_load.utils.ByteUtils.intToBytes;
import static com.mehatronics.axle_load.utils.ByteUtils.intToFourBytes;
import static com.mehatronics.axle_load.utils.ByteUtils.intToTwoBytes;
import static com.mehatronics.axle_load.utils.ByteUtils.stringToBytes;
import static com.mehatronics.axle_load.utils.DataUtils.convertBytesToBattery;
import static com.mehatronics.axle_load.utils.DataUtils.convertBytesToDate;
import static com.mehatronics.axle_load.utils.DataUtils.convertBytesToString;
import static com.mehatronics.axle_load.utils.DataUtils.convertBytesToValue;

import com.mehatronics.axle_load.entities.CalibrationTable;
import com.mehatronics.axle_load.entities.DeviceDetails;
import com.mehatronics.axle_load.entities.SensorConfig;

import java.util.List;

import javax.inject.Inject;

public class GattDataParser {

    @Inject
    public GattDataParser() {
    }

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
                .setTable(table)
                .build();
    }

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
}
