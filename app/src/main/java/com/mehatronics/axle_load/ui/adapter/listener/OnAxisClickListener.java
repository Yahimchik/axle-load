package com.mehatronics.axle_load.ui.adapter.listener;

import com.mehatronics.axle_load.domain.entities.enums.AxisSide;

public interface OnAxisClickListener {
    void onClick(int axisNumber, AxisSide side, boolean isSavedState, boolean isSelected);
}