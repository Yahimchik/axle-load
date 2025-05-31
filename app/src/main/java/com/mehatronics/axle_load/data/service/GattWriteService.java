package com.mehatronics.axle_load.data.service;

import android.bluetooth.BluetoothGatt;

public interface GattWriteService {
    void setCommand(int c1, int c2);

    byte[] getBuffer();

    void clearBuffer();

    void write(BluetoothGatt gatt);

}
