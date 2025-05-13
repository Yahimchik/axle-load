package com.mehatronics.axle_load.ui.binder;

import android.view.View;

import com.mehatronics.axle_load.entities.DeviceDetails;
import com.mehatronics.axle_load.entities.SensorConfig;

public interface DeviceDetailsBinder {
    void bind(DeviceDetails deviceDetails);
    void bindConfigure(SensorConfig sensorConfig);
    void setupSaveButton(View.OnClickListener listener);
}
