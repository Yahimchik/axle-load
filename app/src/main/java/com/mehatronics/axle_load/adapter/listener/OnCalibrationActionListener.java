package com.mehatronics.axle_load.adapter.listener;

import com.mehatronics.axle_load.entities.CalibrationTable;

public interface OnCalibrationActionListener {
    void onAdd(CalibrationTable table, int position);
    void onDelete(CalibrationTable table, int position);
}

