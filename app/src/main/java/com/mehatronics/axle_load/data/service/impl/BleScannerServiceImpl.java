package com.mehatronics.axle_load.data.service.impl;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mehatronics.axle_load.domain.entities.device.Device;
import com.mehatronics.axle_load.domain.entities.enums.DeviceType;
import com.mehatronics.axle_load.data.service.BleScannerService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BleScannerServiceImpl implements BleScannerService {
    private final MutableLiveData<List<Device>> scannedDevices = new MutableLiveData<>(new ArrayList<>());
    private BluetoothLeScanner bleScanner;
    private DeviceType deviceType;

    @Inject
    public BleScannerServiceImpl(BluetoothAdapter bluetoothAdapter) {
        if (bluetoothAdapter != null) {
            this.bleScanner = bluetoothAdapter.getBluetoothLeScanner();
        }
    }

    public LiveData<List<Device>> getScannedDevices() {
        return scannedDevices;
    }

    public void clearScannedDevices() {
        scannedDevices.setValue(new ArrayList<>());
    }

    public void removeDeviceByAddress(String address) {
        List<Device> currentList = new ArrayList<>(Objects.requireNonNull(scannedDevices.getValue()));
        boolean removed = currentList.removeIf(device ->
                device.getDevice().getAddress().equals(address));
        if (removed) {
            scannedDevices.postValue(currentList);
            Log.d("MyTag", "Device removed from scan list: " + address);
        }
    }

    public void startScan(DeviceType deviceType) {
        if (bleScanner != null) {
            try {
                this.deviceType = deviceType;
                ScanSettings settings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .build();

                bleScanner.startScan(null, settings, scanCallback);
                Log.d("MyTag", "Start scanning...\nAvailable devices");
            } catch (SecurityException e) {
                Log.e("MyTag", "Start scan failed: SecurityException", e);
            }
        }
    }

    public void stopScan() {
        if (bleScanner != null) {
            try {
                bleScanner.stopScan(scanCallback);
                Log.d("MyTag", "Scanning is stopped");
            } catch (SecurityException e) {
                Log.e("BleScanner", "Stop scan failed: SecurityException", e);
            }
        }
    }

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (result == null || result.getDevice() == null) {
                return;
            }

            BluetoothDevice device = result.getDevice();

            if (!isDeviceTypeValid(device)) {
                return;
            }

            addOrUpdateDevice(new Device(device, result));
        }
    };

    private void addOrUpdateDevice(Device newDevice) {
        List<Device> currentList = new ArrayList<>(Objects.requireNonNull(scannedDevices.getValue()));
        boolean updated = false;
        for (int i = 0; i < currentList.size(); i++) {
            Device device = currentList.get(i);
            if (isDeviceExist(newDevice, device)) {
                currentList.set(i, newDevice);
                updated = true;
                break;
            }
        }
        if (!updated) {
            currentList.add(newDevice);
            try {
                Log.d("MyTag", newDevice.getDevice().getName());
            } catch (SecurityException e) {
                Log.d("MyTag", "Security exception: " + e.getMessage());
            }
        }

        scannedDevices.postValue(currentList);
    }

    private static boolean isDeviceExist(Device newDevice, Device device) {
        return device.getDevice().getAddress()
                .equals(newDevice.getDevice().getAddress());
    }

    private boolean isDeviceTypeValid(BluetoothDevice device) {
        try {
            return device.getName() != null && device.getName().contains(deviceType.name());
        } catch (SecurityException e) {
            Log.e("BleScanner", "Device type is invalid", e);
        }
        return false;
    }
}




