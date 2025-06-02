package com.mehatronics.axle_load.domain.handler;

import static com.mehatronics.axle_load.R.string.connection_failed;
import static com.mehatronics.axle_load.R.string.selected;
import static java.lang.Boolean.TRUE;

import android.Manifest;

import androidx.annotation.RequiresPermission;

import com.mehatronics.axle_load.domain.entities.device.Device;
import com.mehatronics.axle_load.domain.entities.device.DeviceDetails;
import com.mehatronics.axle_load.localization.ResourceProvider;
import com.mehatronics.axle_load.ui.viewModel.DeviceViewModel;

import javax.inject.Inject;

public class BluetoothHandler {
    private final DeviceViewModel deviceViewModel;
    private final BluetoothHandlerContract activity;
    private final ResourceProvider resourceProvider;
    private boolean userClosedDeviceDetails = false;

    @Inject
    public BluetoothHandler(DeviceViewModel deviceViewModel, BluetoothHandlerContract activity, ResourceProvider resourceProvider) {
        this.deviceViewModel = deviceViewModel;
        this.activity = activity;
        this.resourceProvider = resourceProvider;
    }

    public void onDeviceDetailsFragmentClosed() {
        userClosedDeviceDetails = true;
    }

    public void onDeviceDetailsFragmentOpen() {
        userClosedDeviceDetails = false;
    }

    public void handleDeviceDetails(DeviceDetails deviceDetails) {
        activity.loadingManagerShowLoading(false);
        if (deviceDetails != null && isConnected()) {
            if (!userClosedDeviceDetails) {
                activity.showFragment();
            }
        } else {
            userClosedDeviceDetails = false;
        }
    }

    public void handleConnectionState(Boolean isConnected) {
        if (!isConnected && activity.isAttemptingToConnect()) {
            activity.showMessage(resourceProvider.getString(connection_failed));
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
        activity.showMessage(resourceProvider.getString(selected, device.getDevice().getName()));
        activity.onFragmentOpen();
        deviceViewModel.connectToDevice(device);
    }

    private boolean isConnected() {
        return TRUE.equals(deviceViewModel.isConnectedLiveData().getValue());
    }
}
