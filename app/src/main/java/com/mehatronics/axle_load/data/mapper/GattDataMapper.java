package com.mehatronics.axle_load.data.mapper;

import android.bluetooth.BluetoothGatt;

import com.mehatronics.axle_load.domain.entities.AxisModel;
import com.mehatronics.axle_load.domain.entities.CalibrationTable;
import com.mehatronics.axle_load.domain.entities.device.BTCOMMiniDetails;
import com.mehatronics.axle_load.domain.entities.device.DeviceDetails;
import com.mehatronics.axle_load.domain.entities.SensorConfig;
import com.mehatronics.axle_load.domain.entities.device.DeviceInfoToSave;

import java.util.List;

public interface GattDataMapper {
    DeviceDetails convertToDeviceDetails(BluetoothGatt gatt, List<byte[]> values, List<CalibrationTable> table);

    void setConfigureSettings(SensorConfig sensorConfig, byte[] buffer);

    void setBTCOMMiniSettings(DeviceInfoToSave save, byte[] buffer);

    int setCalibrationTable(List<CalibrationTable> table, byte[] buffer, int page);

    List<AxisModel> convertToAxisModelList(byte[] buffer);
}
