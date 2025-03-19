package com.mehatronics.axle_load.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mehatronics.axle_load.entities.Device;
import com.mehatronics.axle_load.entities.enums.DeviceType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BleScanner {
    private final MutableLiveData<List<Device>> scannedDevices = new MutableLiveData<>(new ArrayList<>());
    private final Set<String> deviceAddresses = new HashSet<>();
    private BluetoothLeScanner bleScanner;
    private DeviceType deviceType;

    @Inject
    public BleScanner(BluetoothAdapter bluetoothAdapter) {
        if (bluetoothAdapter != null) {
            this.bleScanner = bluetoothAdapter.getBluetoothLeScanner();
        }
    }

    public LiveData<List<Device>> getScannedDevices() {
        return scannedDevices;
    }

    public void clearScannedDevices() {
        deviceAddresses.clear();
        scannedDevices.setValue(new ArrayList<>());
    }

    public void startScan(DeviceType deviceType) {
        if (bleScanner != null) {
            try {
                this.deviceType = deviceType;
                ScanSettings settings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .build();

                bleScanner.startScan(null, settings, scanCallback);
            } catch (SecurityException e) {
                Log.e("BleScanner", "Start scan failed: SecurityException", e);
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
            String deviceAddress = device.getAddress();

            if (!isDeviceTypeValid(device)) {
                return;
            }

            addOrUpdateDevice(new Device(device, result));
            deviceAddresses.add(deviceAddress);
        }
    };

    private void addOrUpdateDevice(Device newDevice) {
        List<Device> currentList = new ArrayList<>(Objects.requireNonNull(scannedDevices.getValue()));
        boolean updated = false;
        for (int i = 0; i < currentList.size(); i++) {
            Device device = currentList.get(i);
            if (device.getDevice().getAddress().equals(newDevice.getDevice().getAddress())) {
                currentList.set(i, newDevice);
                updated = true;
                break;
            }
        }
        if (!updated) {
            currentList.add(newDevice);
        }
        scannedDevices.postValue(currentList);
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




