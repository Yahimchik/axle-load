package com.mehatronics.axle_load.ble.repository;

import androidx.lifecycle.LiveData;

import com.mehatronics.axle_load.ble.BleScanner;
import com.mehatronics.axle_load.ble.BluetoothConnectionManager;
import com.mehatronics.axle_load.ble.CalibrationTableManager;
import com.mehatronics.axle_load.entities.CalibrationTable;
import com.mehatronics.axle_load.entities.Device;
import com.mehatronics.axle_load.entities.DeviceDetails;
import com.mehatronics.axle_load.entities.SensorConfig;
import com.mehatronics.axle_load.entities.enums.DeviceType;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BluetoothRepository {
    private final BluetoothConnectionManager bluetoothConnectionManager;
    private final CalibrationTableManager calibrationTableManager;
    private final BleScanner bleScanner;

    @Inject
    public BluetoothRepository(BluetoothConnectionManager bluetoothConnectionManager, BleScanner bleScanner, CalibrationTableManager calibrationTableManager) {
        this.bluetoothConnectionManager = bluetoothConnectionManager;
        this.calibrationTableManager = calibrationTableManager;
        this.bleScanner = bleScanner;
    }

    public LiveData<List<Device>> getScannedDevices() {
        return bleScanner.getScannedDevices();
    }

    public LiveData<Boolean> isConnectedLiveData() {
        return bluetoothConnectionManager.isConnectedLiveData();
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

    public void setDeviceDetailsLiveData(DeviceDetails details) {
        bluetoothConnectionManager.setDeviceDetailsLiveData(details);
    }

    public LiveData<SensorConfig> getSensorConfigureLiveData() {
        return bluetoothConnectionManager.getSensorConfigureLiveData();
    }

    public void connectToDevice(Device device) {
        bluetoothConnectionManager.connectToDevice(device);
    }

    public void saveConfiguration() {
        bluetoothConnectionManager.saveConfiguration();
    }

    public void rereadCalibrationTable() {
        bluetoothConnectionManager.rereadCalibrationTable();
    }

    public void disconnect() {
        bluetoothConnectionManager.disconnect();
    }

    public void clearDetails() {
        bluetoothConnectionManager.clearDetails();
    }

    public LiveData<List<CalibrationTable>> getCalibrationTable() {
        return calibrationTableManager.getCalibrationTable();
    }

    public void updateVirtualPoint(DeviceDetails deviceDetails) {
        calibrationTableManager.updateVirtualPoint(deviceDetails);
    }

    public void deletePoint(CalibrationTable item) {
        calibrationTableManager.deletePoint(item);
    }

    public void addPoint(CalibrationTable newPoint) {
        calibrationTableManager.addPoint(newPoint);
    }

    public int saveTable() {
        return calibrationTableManager.convertMultiplier();
    }
}
