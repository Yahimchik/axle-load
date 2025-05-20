package com.mehatronics.axle_load.adapter.listener;

import com.mehatronics.axle_load.entities.CalibrationTable;

@FunctionalInterface
public interface OnAddCalibrationPointListener {
    void onAdd( CalibrationTable referencePoint);
}
