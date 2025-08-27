package com.mehatronics.axle_load.data.service.impl;

import static com.mehatronics.axle_load.constants.BundleKeys.AXIS_NUMBER;
import static com.mehatronics.axle_load.constants.BundleKeys.AXIS_SIDE;
import static com.mehatronics.axle_load.constants.BundleKeys.MAC;
import static com.mehatronics.axle_load.constants.BundleKeys.RESULT_KEY;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;

import com.mehatronics.axle_load.data.service.SensorSelectionService;
import com.mehatronics.axle_load.domain.entities.InstalationPoint;
import com.mehatronics.axle_load.domain.entities.device.Device;
import com.mehatronics.axle_load.domain.entities.enums.AxisSide;
import com.mehatronics.axle_load.ui.adapter.listener.OnDeviceSelectionCallback;
import com.mehatronics.axle_load.ui.navigation.FragmentNavigator;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SensorSelectionServiceImpl implements SensorSelectionService {

    @Inject
    public SensorSelectionServiceImpl() {
    }

    @Override
    public void observeSelectedDevice(
            @NonNull FragmentManager fragmentManager,
            @NonNull LifecycleOwner lifecycleOwner,
            @NonNull OnDeviceSelectionCallback callback
    ) {
        fragmentManager.setFragmentResultListener(RESULT_KEY, lifecycleOwner, (key, bundle) -> {
            String mac = bundle.getString(MAC);
            String sideStr = bundle.getString(AXIS_SIDE);
            int axisNumber = bundle.getInt(AXIS_NUMBER);
            callback.onDeviceSelected(mac, axisNumber, sideStr);
        });
    }

    @Override
    public void returnSelectedDevice(
            @NonNull FragmentManager fragmentManager,
            @NonNull FragmentActivity activity,
            int axisNumber,
            @NonNull AxisSide axisSide,
            @NonNull Device device
    ) {
        Bundle result = new Bundle();
        result.putString(MAC, device.getDevice().getAddress());
        result.putInt(AXIS_NUMBER, axisNumber);
        result.putString(AXIS_SIDE, axisSide.name());
        fragmentManager.setFragmentResult(RESULT_KEY, result);
        activity.getSupportFragmentManager().popBackStack();
    }

    @Override
    public void openSensorSelection(@NonNull FragmentNavigator navigator, @NonNull InstalationPoint point, @NonNull Fragment fragment) {
        Bundle args = new Bundle();
        args.putInt(AXIS_NUMBER, point.axleNumber());
        args.putString(AXIS_SIDE, point.position().name());
        fragment.setArguments(args);
        navigator.showFragment(fragment);
    }
}