package com.mehatronics.axle_load.ble.strategy;

public interface CommandStrategy {
    void fillBuffer(byte[] buffer);
}
