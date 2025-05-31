package com.mehatronics.axle_load.domain.handler;

import android.bluetooth.BluetoothDevice;

import com.mehatronics.axle_load.domain.entities.device.Device;

public interface ConnectionHandler {
    void connect(Device device);

    void reconnect(BluetoothDevice device);

    void onConnected();

    void disconnect();
}

