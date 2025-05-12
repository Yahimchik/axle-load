package com.mehatronics.axle_load.viewModel;

import android.view.View;

import com.mehatronics.axle_load.entities.CalibrationTable;
import com.mehatronics.axle_load.entities.DeviceDetails;
import com.mehatronics.axle_load.entities.SensorConfig;

import java.util.List;

public interface DeviceDetailsBinder {
    void bindDeviceDetails(DeviceDetails deviceDetails);
    void bindSensorConfig(SensorConfig config);
    void setSaveListener(View.OnClickListener listener);
    void updateCalibrationTable(List<CalibrationTable> table);
    void updateVirtualPoint(CalibrationTable virtualPoint);
}
