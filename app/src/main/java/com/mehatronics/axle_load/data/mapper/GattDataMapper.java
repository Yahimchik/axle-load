package com.mehatronics.axle_load.data.mapper;

import android.bluetooth.BluetoothGatt;

import com.mehatronics.axle_load.domain.entities.CalibrationTable;
import com.mehatronics.axle_load.domain.entities.device.DeviceDetails;
import com.mehatronics.axle_load.domain.entities.SensorConfig;

import java.util.List;

public interface GattDataMapper {
    DeviceDetails convertToDeviceDetails(BluetoothGatt gatt, List<byte[]> values, List<CalibrationTable> table);

    void setConfigureSettings(SensorConfig sensorConfig, byte[] buffer);

    int setCalibrationTable(List<CalibrationTable> table, byte[] buffer, int page);
}
