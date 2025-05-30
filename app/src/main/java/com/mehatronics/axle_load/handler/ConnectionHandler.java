package com.mehatronics.axle_load.handler;

import android.bluetooth.BluetoothDevice;

import com.mehatronics.axle_load.entities.Device;

public interface ConnectionHandler {
    void connect(Device device);

    void reconnect(BluetoothDevice device);

    void onConnected();

    void disconnect();
}

