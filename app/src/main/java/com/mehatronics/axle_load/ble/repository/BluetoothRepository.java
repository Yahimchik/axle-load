package com.mehatronics.axle_load.ble.repository;

import androidx.lifecycle.LiveData;

import com.mehatronics.axle_load.ble.BleScanner;
import com.mehatronics.axle_load.ble.BluetoothConnectionManager;
import com.mehatronics.axle_load.entities.Device;
import com.mehatronics.axle_load.entities.DeviceDetails;
import com.mehatronics.axle_load.entities.enums.DeviceType;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BluetoothRepository {
    private final BluetoothConnectionManager bluetoothConnectionManager;
    private final BleScanner bleScanner;

    @Inject
    public BluetoothRepository(BluetoothConnectionManager bluetoothConnectionManager, BleScanner bleScanner) {
        this.bluetoothConnectionManager = bluetoothConnectionManager;
        this.bleScanner = bleScanner;
    }

    public LiveData<List<Device>> getScannedDevices() {
        return bleScanner.getScannedDevices();
    }

    public void clearScannedDevices() {
        bleScanner.clearScannedDevices();
    }

    public void startScan(DeviceType deviceType) {
        bleScanner.startScan(deviceType);
    }

    public void stopScan() {
        bleScanner.stopScan();
    }

    public LiveData<DeviceDetails> getDeviceDetailsLiveData() {
        return bluetoothConnectionManager.getDeviceDetailsLiveData();
    }

    public void connectToDevice(Device device) {
        bluetoothConnectionManager.connectToDevice(device);
    }

    public void disconnect() {
        bluetoothConnectionManager.disconnect();
    }

    public void clearDetails() {
        bluetoothConnectionManager.clearDetails();
    }

    public boolean isConnected() {
        return bluetoothConnectionManager.isConnected();
    }
}
