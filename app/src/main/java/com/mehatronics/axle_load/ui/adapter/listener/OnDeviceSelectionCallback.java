package com.mehatronics.axle_load.ui.adapter.listener;

public interface OnDeviceSelectionCallback {
    void onDeviceSelected(String mac, int axisNumber, String axisSide);
}