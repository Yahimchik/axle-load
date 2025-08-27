package com.mehatronics.axle_load.domain.strategy;

public interface CommandStrategy {
    void fillBuffer(byte[] buffer);
}