package com.mehatronics.axle_load.ble.handler;

import static java.lang.Boolean.TRUE;

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
        if (deviceDetails != null && TRUE.equals(deviceViewModel.isConnectedLiveData().getValue())) {
            if (activity.isFragmentNotVisible()) {
                activity.showFragment();
            }
        }
    }

    public void handleConnectionState(Boolean isConnected) {
        if (!isConnected && activity.isAttemptingToConnect()) {
            activity.showSnackBar("Failed to connect to device");
            activity.loadingManagerShowLoading(false);
            activity.setIsAttemptingToConnect(false);
        }

        if (isConnected) {
            activity.setIsAttemptingToConnect(false);
        }
    }

    public void onDeviceSelected(Device device) {
        activity.loadingManagerShowLoading(true);
        activity.setIsAttemptingToConnect(true);
        deviceViewModel.connectToDevice(device);
    }
}
