package com.mehatronics.axle_load.data.service;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;

import com.mehatronics.axle_load.domain.entities.InstalationPoint;
import com.mehatronics.axle_load.domain.entities.device.Device;
import com.mehatronics.axle_load.domain.entities.enums.AxisSide;
import com.mehatronics.axle_load.ui.adapter.listener.OnDeviceSelectionCallback;
import com.mehatronics.axle_load.ui.navigation.FragmentNavigator;

public interface SensorSelectionService {
    void observeSelectedDevice(
            @NonNull FragmentManager fm,
            @NonNull LifecycleOwner owner,
            @NonNull OnDeviceSelectionCallback cb
    );

    void returnSelectedDevice(
            @NonNull FragmentManager fm,
            @NonNull FragmentActivity activity,
            int axisNumber,
            @NonNull AxisSide axisSide,
            @NonNull Device device
    );

    void openSensorSelection(
            @NonNull FragmentNavigator navigator,
            @NonNull InstalationPoint point,
            @NonNull Fragment fragment
    );
}
