package com.mehatronics.axle_load.domain.entities.device;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Device implements Serializable {
    private final BluetoothDevice device;
    private final ScanResult scanResult;
    private boolean isSelected;
    private final String macAddress;

    public Device(BluetoothDevice device, ScanResult scanResult) {
        this.device = device;
        this.scanResult = scanResult;
        this.macAddress = device.getAddress();
    }

    public Device(String macAddress) {
        this.device = null;
        this.scanResult = null;
        this.macAddress = macAddress;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Device device = (Device) obj;
        return getDevice().getAddress().equals(device.getDevice().getAddress());
    }

    @Override
    public int hashCode() {
        return getDevice().getAddress().hashCode();
    }
}