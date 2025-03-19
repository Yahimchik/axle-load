package com.mehatronics.axle_load.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.mehatronics.axle_load.ble.repository.BluetoothRepository;
import com.mehatronics.axle_load.entities.Device;
import com.mehatronics.axle_load.entities.DeviceDetails;
import com.mehatronics.axle_load.entities.enums.DeviceType;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class BluetoothViewModel extends ViewModel {
    private final BluetoothRepository bluetoothRepository;

    @Inject
    public BluetoothViewModel(BluetoothRepository bluetoothRepository) {
        this.bluetoothRepository = bluetoothRepository;
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

    public void connectToDevice(Device device) {
        bluetoothRepository.connectToDevice(device);
    }

    public void disconnect() {
        bluetoothRepository.disconnect();
    }

    public void clearDetails() {
        bluetoothRepository.clearDetails();
    }

    public boolean isConnected() {
        return bluetoothRepository.isConnected();
    }
}

