package com.mehatronics.axle_load.data.service;

import androidx.lifecycle.LiveData;

import com.mehatronics.axle_load.domain.entities.device.Device;
import com.mehatronics.axle_load.domain.entities.enums.DeviceType;

import java.util.List;

public interface BleScannerService {
    LiveData<List<Device>> getScannedDevices();

    void clearScannedDevices();

    void startScan(DeviceType deviceType);

    void stopScan();
}
