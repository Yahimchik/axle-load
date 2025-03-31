package com.mehatronics.axle_load.entities;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Device implements Serializable {
    private final BluetoothDevice device;
    private final ScanResult scanResult;

    public Device(BluetoothDevice device, ScanResult scanResult) {
        this.device = device;
        this.scanResult = scanResult;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public ScanResult getScanResult() {
        return scanResult;
    }

    @NonNull
    @Override
    public String toString() {
        return "Device{" +
                "device=" + device +
                ", scanResult=" + scanResult +
                '}';
    }
}

