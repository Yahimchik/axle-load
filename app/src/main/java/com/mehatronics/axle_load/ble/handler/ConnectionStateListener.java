package com.mehatronics.axle_load.ble.handler;

public interface ConnectionStateListener {
    void onConnected();
    void onDisconnected();
}

