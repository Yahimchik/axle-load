package com.mehatronics.axle_load.ble.handler;

import static java.lang.Boolean.TRUE;

import com.mehatronics.axle_load.entities.Device;
import com.mehatronics.axle_load.entities.DeviceDetails;
import com.mehatronics.axle_load.viewModel.BluetoothViewModel;

import javax.inject.Inject;

public class BluetoothHandler {
    private final BluetoothViewModel bluetoothViewModel;
    private final BluetoothHandlerContract activity;

    @Inject
    public BluetoothHandler(BluetoothViewModel bluetoothViewModel, BluetoothHandlerContract activity) {
        this.bluetoothViewModel = bluetoothViewModel;
        this.activity = activity;
    }

    public void handleDeviceDetails(DeviceDetails deviceDetails) {
        activity.loadingManagerShowLoading(false);
        if (deviceDetails != null && TRUE.equals(bluetoothViewModel.isConnectedLiveData().getValue())) {
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
        bluetoothViewModel.connectToDevice(device);
    }
}
