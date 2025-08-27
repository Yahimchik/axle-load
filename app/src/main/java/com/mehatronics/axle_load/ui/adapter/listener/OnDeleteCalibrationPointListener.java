package com.mehatronics.axle_load.ui.adapter.listener;

import com.mehatronics.axle_load.domain.entities.CalibrationTable;

@FunctionalInterface
public interface OnDeleteCalibrationPointListener {
    void onDelete(CalibrationTable point);
}