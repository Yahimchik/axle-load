package com.mehatronics.axle_load.ble.handler;

import static java.lang.Boolean.TRUE;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;

import com.mehatronics.axle_load.entities.Device;
import com.mehatronics.axle_load.entities.DeviceDetails;
import com.mehatronics.axle_load.viewModel.DeviceViewModel;

import javax.inject.Inject;

public class BluetoothHandler {
    private final DeviceViewModel deviceViewModel;
    private final BluetoothHandlerContract activity;

    @Inject
    public BluetoothHandler(DeviceViewModel deviceViewModel, BluetoothHandlerContract activity) {
        this.deviceViewModel = deviceViewModel;
        this.activity = activity;
    }

    public void handleDeviceDetails(DeviceDetails deviceDetails) {
        activity.loadingManagerShowLoading(false);
        if (deviceDetails != null && isConnected()) {
            if (activity.isFragmentNotVisible()) {
                activity.showFragment();
            }
        }
    }

    public void handleConnectionState(Boolean isConnected) {
        if (!isConnected && activity.isAttemptingToConnect()) {
            activity.showMessage("Failed to connect to device");
            activity.loadingManagerShowLoading(false);
            activity.setIsAttemptingToConnect(false);
        }

        if (isConnected) {
            activity.setIsAttemptingToConnect(false);
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public void onDeviceSelected(Device device) {
        activity.loadingManagerShowLoading(true);
        activity.setIsAttemptingToConnect(true);
        activity.showMessage("Selected " + device.getDevice().getName());
        deviceViewModel.connectToDevice(device);
    }

    private boolean isConnected() {
        return TRUE.equals(deviceViewModel.isConnectedLiveData().getValue());
    }
}
