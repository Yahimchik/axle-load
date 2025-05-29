package com.mehatronics.axle_load.adapter.listener;

import com.mehatronics.axle_load.entities.enums.AxisSide;

public interface OnAxisClickListener {
    void onClick(int axisNumber, AxisSide side);
}
