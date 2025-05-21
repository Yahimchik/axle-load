package com.mehatronics.axle_load.viewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.mehatronics.axle_load.ble.repository.BluetoothRepository;
import com.mehatronics.axle_load.entities.CalibrationTable;
import com.mehatronics.axle_load.entities.Device;
import com.mehatronics.axle_load.entities.DeviceDetails;
import com.mehatronics.axle_load.entities.SensorConfig;
import com.mehatronics.axle_load.entities.enums.DeviceType;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class DeviceViewModel extends ViewModel {
    private final BluetoothRepository bluetoothRepository;

    @Inject
    public DeviceViewModel(BluetoothRepository bluetoothRepository) {
        this.bluetoothRepository = bluetoothRepository;
    }

    public LiveData<Boolean> isConnectedLiveData() {
        return bluetoothRepository.isConnectedLiveData();
    }

    public LiveData<List<Device>> getScannedDevices() {
        return bluetoothRepository.getScannedDevices();
    }

    public void clearScannedDevices() {
        bluetoothRepository.clearScannedDevices();
    }

    public void startScan(DeviceType deviceType) {
        bluetoothRepository.startScan(deviceType);
    }

    public void stopScan() {
        bluetoothRepository.stopScan();
    }

    public LiveData<DeviceDetails> getDeviceDetails() {
        return bluetoothRepository.getDeviceDetailsLiveData();
    }

    public void setDeviceDetailsLiveData(DeviceDetails details) {
        bluetoothRepository.setDeviceDetailsLiveData(details);
    }

    public LiveData<SensorConfig> getSensorConfigure() {
        return bluetoothRepository.getSensorConfigureLiveData();
    }

    public void connectToDevice(Device device) {
        bluetoothRepository.connectToDevice(device);
    }

    public void disconnect() {
        bluetoothRepository.disconnect();
    }

    public void clearDetails() {
        bluetoothRepository.clearDetails();
    }

    public void saveSensorConfiguration() {
        if (getSensorConfigure().getValue() != null) {
            bluetoothRepository.saveConfiguration();
        } else {
            Log.w("MyTag", "Sensor config is null");
        }
    }

    public void rereadCalibrationTable() {
        bluetoothRepository.rereadCalibrationTable();
    }

    public LiveData<List<CalibrationTable>> getCalibrationTable() {
        return bluetoothRepository.getCalibrationTable();
    }

    public void updateVirtualPoint(DeviceDetails deviceDetails) {
        bluetoothRepository.updateVirtualPoint(deviceDetails);
    }

    public void deletePoint(CalibrationTable item) {
        bluetoothRepository.deletePoint(item);
    }

    public void addPoint(CalibrationTable newPoint) {
        bluetoothRepository.addPoint(newPoint);
    }
}

