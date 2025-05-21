package com.mehatronics.axle_load.adapter.listener;

import com.mehatronics.axle_load.entities.CalibrationTable;

@FunctionalInterface
public interface OnDeleteCalibrationPointListener {
    void onDelete(CalibrationTable point);
}
