package com.mehatronics.axle_load.ui.adapter.listener;

import com.mehatronics.axle_load.domain.entities.enums.AxisSide;

public interface OnAxisConnectListener {
    void onConnect(int axisNumber, AxisSide side);
}