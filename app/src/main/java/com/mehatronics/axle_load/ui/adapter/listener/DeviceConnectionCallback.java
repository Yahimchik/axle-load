package com.mehatronics.axle_load.ui.adapter.listener;

public interface DeviceConnectionCallback {
    void onSuccess();
    void onFailure(String errorMessage);
}

