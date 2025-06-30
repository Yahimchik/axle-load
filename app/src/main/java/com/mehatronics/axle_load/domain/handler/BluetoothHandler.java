package com.mehatronics.axle_load.domain.handler;

import static com.mehatronics.axle_load.R.string.connection_failed;
import static com.mehatronics.axle_load.R.string.selected;
import static java.lang.Boolean.TRUE;

import android.Manifest;

import androidx.annotation.RequiresPermission;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.domain.entities.device.Device;
import com.mehatronics.axle_load.domain.entities.device.DeviceDetails;
import com.mehatronics.axle_load.localization.ResourceProvider;
import com.mehatronics.axle_load.ui.viewModel.DeviceViewModel;

import javax.inject.Inject;

public class BluetoothHandler {
    private final DeviceViewModel deviceViewModel;
    private final BluetoothHandlerContract contract;
    private final ResourceProvider resourceProvider;
    private boolean userClosedDeviceDetails = false;
    private String deviceName;

    @Inject
    public BluetoothHandler(DeviceViewModel deviceViewModel, BluetoothHandlerContract contract, ResourceProvider resourceProvider) {
        this.deviceViewModel = deviceViewModel;
        this.contract = contract;
        this.resourceProvider = resourceProvider;
    }

    public void onDeviceDetailsFragmentClosed() {
        userClosedDeviceDetails = true;
        deviceViewModel.disconnect();
        deviceViewModel.clearDetails();
        contract.showMessage(resourceProvider.getString(R.string.disconnect_from, deviceName));
    }

    public void onDeviceDetailsFragmentOpen() {
        userClosedDeviceDetails = false;
    }

    public void handleDeviceDetails(DeviceDetails deviceDetails) {
        contract.loadingManagerShowLoading(false);
        if (deviceDetails != null && isConnected()) {
            deviceName = deviceDetails.getDeviceName();
            if (!userClosedDeviceDetails) {
                contract.showFragment();
            }
        } else {
            userClosedDeviceDetails = false;
        }
    }

    public void handleConnectionState(Boolean isConnected) {
        if (!isConnected && contract.isAttemptingToConnect()) {
            contract.showMessage(resourceProvider.getString(connection_failed));
            contract.loadingManagerShowLoading(false);
            contract.setIsAttemptingToConnect(false);
        }

        if (isConnected) {
            contract.setIsAttemptingToConnect(false);
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public void onDeviceSelected(Device device) {
        contract.loadingManagerShowLoading(true);
        contract.setIsAttemptingToConnect(true);
        contract.showMessage(resourceProvider.getString(selected, device.getDevice().getName()));
        contract.onFragmentOpen();
        deviceViewModel.connectToDevice(device);
    }

    private boolean isConnected() {
        return TRUE.equals(deviceViewModel.isConnectedLiveData().getValue());
    }
}
