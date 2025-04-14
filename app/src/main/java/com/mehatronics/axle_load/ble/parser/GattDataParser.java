package com.mehatronics.axle_load.ble.parser;

import static com.mehatronics.axle_load.entities.enums.CharacteristicType.PRESSURE;
import static com.mehatronics.axle_load.entities.enums.CharacteristicType.WEIGHT;
import static com.mehatronics.axle_load.utils.DataUtils.convertBytesToBattery;
import static com.mehatronics.axle_load.utils.DataUtils.convertBytesToDate;
import static com.mehatronics.axle_load.utils.DataUtils.convertBytesToString;
import static com.mehatronics.axle_load.utils.DataUtils.convertBytesToValue;

import com.mehatronics.axle_load.entities.CalibrationTable;
import com.mehatronics.axle_load.entities.DeviceDetails;

import java.util.List;

public class GattDataParser {
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
        String weight = convertBytesToValue(values.get(values.size() - 1), WEIGHT);
        String pressure = convertBytesToValue(values.get(values.size() - 1), PRESSURE);

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
}
