package com.mehatronics.axle_load.data.service.impl;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.RequiresPermission;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mehatronics.axle_load.data.service.BleScannerService;
import com.mehatronics.axle_load.domain.entities.device.Device;
import com.mehatronics.axle_load.domain.entities.enums.DeviceType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BleScannerServiceImpl implements BleScannerService {
    private static final long SCAN_INTERVAL = 30000;
    private final MutableLiveData<List<Device>> scannedDevices = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Device>> btComMiniDevices = new MutableLiveData<>(new ArrayList<>());
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable restartScanRunnable = this::restartScan;
    private BluetoothLeScanner bleScanner;
    private DeviceType deviceType;

    @Inject
    public BleScannerServiceImpl(BluetoothAdapter bluetoothAdapter) {
        if (bluetoothAdapter != null) {
            this.bleScanner = bluetoothAdapter.getBluetoothLeScanner();
        }
    }

    @Override
    public LiveData<List<Device>> getScannedDevices() {
        return scannedDevices;
    }

    @Override
    public LiveData<List<Device>> getBtComMiniDevices() {
        return btComMiniDevices;
    }

    @Override
    public void clearScannedDevices() {
        scannedDevices.setValue(new ArrayList<>());
    }

    @Override
    public void removeDeviceByAddress(String address) {
        List<Device> currentList = new ArrayList<>(Objects.requireNonNull(scannedDevices.getValue()));
        boolean removed = currentList.removeIf(device ->
                device.getDevice().getAddress().equals(address));
        if (removed) {
            scannedDevices.postValue(currentList);
            Log.d("MyTag", "Device removed from scan list: " + address);
        }
    }

    @Override
    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    public void startScan(DeviceType deviceType) {
        this.deviceType = deviceType;
        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();

        bleScanner.startScan(null, settings, scanCallback);
        Log.d("MyTag", "Start scanning...\nAvailable devices");
        handler.removeCallbacks(restartScanRunnable);
        handler.postDelayed(restartScanRunnable, SCAN_INTERVAL);
    }

    @Override
    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    public void stopScan() {
        bleScanner.stopScan(scanCallback);
        handler.removeCallbacks(restartScanRunnable);
    }

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        public void onScanResult(int callbackType, ScanResult result) {
            if (result == null || result.getDevice() == null) return;

            BluetoothDevice device = result.getDevice();
            if (device.getName() == null) return;

            Device newDevice = new Device(device, result);

            if (device.getName().contains(DeviceType.DPS.toString())) {
                addOrUpdateDeviceDPS(newDevice);
            }

            if (device.getName().contains(DeviceType.BT_COM_MINI.toString())) {
                addOrUpdateDeviceBT(newDevice);
            }
        }
    };

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void addOrUpdateDeviceDPS(Device newDevice) {
        List<Device> currentList = new ArrayList<>(Objects.requireNonNull(scannedDevices.getValue()));
        boolean updated = false;
        for (int i = 0; i < currentList.size(); i++) {
            Device device = currentList.get(i);
            if (isDeviceExist(newDevice, device)) {
                currentList.set(i, newDevice); // обновляем данные
                updated = true;
                break;
            }
        }
        if (!updated) currentList.add(newDevice);
        scannedDevices.postValue(currentList);
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void addOrUpdateDeviceBT(Device newDevice) {
        List<Device> btList = new ArrayList<>(Objects.requireNonNull(btComMiniDevices.getValue()));
        boolean updated = false;
        for (int i = 0; i < btList.size(); i++) {
            Device device = btList.get(i);
            if (isDeviceExist(newDevice, device)) {
                btList.set(i, newDevice); // обновляем данные
                updated = true;
                break;
            }
        }
        if (!updated) btList.add(newDevice);
        btComMiniDevices.postValue(btList);
    }

    private static boolean isDeviceExist(Device newDevice, Device device) {
        return device.getDevice().getAddress()
                .equals(newDevice.getDevice().getAddress());
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    private void restartScan() {
        stopScan();
        startScan(deviceType);
        handler.postDelayed(restartScanRunnable, SCAN_INTERVAL);
    }
}