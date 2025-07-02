package com.mehatronics.axle_load.data.service;

import android.Manifest;

import androidx.annotation.RequiresPermission;
import androidx.lifecycle.LiveData;

import com.mehatronics.axle_load.domain.entities.device.Device;
import com.mehatronics.axle_load.ui.notification.MessageCallback;

import java.util.List;
import java.util.Set;

public interface SensorService {
    void setSnackBarCallback(MessageCallback messageCallback);

    LiveData<List<Device>> getScannedDevicesLiveData();

    void updateScannedDevices(List<Device> newDevices);

    void markMacAsSelected(Device device);

    void resetSelectedDevices();

    void resetSelectedDevicesByMacs(Set<String> macs);

    boolean isMacSelected(String mac);
}
