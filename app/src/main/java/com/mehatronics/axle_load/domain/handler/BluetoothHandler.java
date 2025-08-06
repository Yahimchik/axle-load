package com.mehatronics.axle_load.domain.handler;

import static com.mehatronics.axle_load.R.string.connection_failed;
import static com.mehatronics.axle_load.R.string.selected;
import static java.lang.Boolean.TRUE;

import android.Manifest;

import androidx.annotation.RequiresPermission;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.domain.entities.device.Device;
import com.mehatronics.axle_load.domain.entities.device.DeviceDetails;
import com.mehatronics.axle_load.domain.entities.enums.AxisSide;
import com.mehatronics.axle_load.localization.ResourceProvider;
import com.mehatronics.axle_load.ui.viewModel.DeviceViewModel;

public class BluetoothHandler {
    private final DeviceViewModel viewModel;
    private final BluetoothHandlerContract contract;
    private final ResourceProvider provider;

    private boolean userClosedDeviceDetails = false;
    private boolean isDeviceDetailsFragmentOpen = false;
    private boolean shouldOpenFragmentAfterConnect = false;

    public BluetoothHandler(DeviceViewModel viewModel,
                            BluetoothHandlerContract contract,
                            ResourceProvider provider) {
        this.viewModel = viewModel;
        this.contract = contract;
        this.provider = provider;
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public void onClick(int axisNumber, AxisSide side, boolean isSavedState, boolean isSelected) {
        if (isSavedState) {
            if (isSelected) {
                onConnect(axisNumber, side);
            } else {
                contract.showMessage(provider.getString(R.string.error_sensor_not_selected));
            }
        } else {
            viewModel.onClick(axisNumber, side);
        }
    }

    public void onReset(int axis) {
        var macsToReset = viewModel.getMacsForAxis(axis);
        viewModel.resetDevicesForAxis(axis);
        viewModel.resetSelectedDevicesByMacs(macsToReset);
        viewModel.markAsUnsaved();
        viewModel.clearMacs();
        contract.showMessage(provider.getString(R.string.configure_reset, axis));
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public void onConnect(int axis, AxisSide side) {
        String mac = viewModel.getMacForAxisSide(axis, side);
        if (mac == null) {
            contract.showMessage(provider.getString(R.string.error_sensor_not_selected));
            return;
        }

        var scanned = viewModel.getScannedDevices().getValue();
        if (scanned == null) return;

        for (Device device : scanned) {
            if (device.getDevice().getAddress().equalsIgnoreCase(mac)) {
                onDeviceSelected(device);
                viewModel.setSelectionMode(true);
                return;
            }
        }
        viewModel.markAsSaved();
    }

    public void onConfigureClick(String input) {
        viewModel.onConfigureClicked(input);
        viewModel.markAsUnsaved();
    }

    public void handleDeviceDetails(DeviceDetails deviceDetails) {
        contract.loadingManagerShowLoading(false);

        if (deviceDetails != null && isConnected()) {
            viewModel.setDeviceName(deviceDetails.getDeviceName());

            if (shouldOpenFragmentAfterConnect && !userClosedDeviceDetails && !isDeviceDetailsFragmentOpen) {
                contract.showFragment();
                isDeviceDetailsFragmentOpen = true;
                shouldOpenFragmentAfterConnect = false;
            }

        } else {
            isDeviceDetailsFragmentOpen = false;
            userClosedDeviceDetails = false;
        }
    }

    public void handleConnectionState(Boolean isConnected) {
        if (Boolean.FALSE.equals(isConnected)) {
            isDeviceDetailsFragmentOpen = false;
            userClosedDeviceDetails = false;
            shouldOpenFragmentAfterConnect = false;
        }

        if (!isConnected && contract.isAttemptingToConnect()) {
            contract.showMessage(provider.getString(connection_failed));
            contract.loadingManagerShowLoading(false);
            contract.setIsAttemptingToConnect(false);
        }

        if (Boolean.TRUE.equals(isConnected)) {
            contract.setIsAttemptingToConnect(false);
            shouldOpenFragmentAfterConnect = true;
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public void onDeviceSelected(Device device) {
        contract.loadingManagerShowLoading(true);
        contract.setIsAttemptingToConnect(true);

        contract.showMessage(provider.getString(selected, device.getDevice().getName()));

        viewModel.setLastFinishedMac(device.getDevice().getAddress());
        viewModel.connectToDevice(device);
    }

    private boolean isConnected() {
        return TRUE.equals(viewModel.isConnectedLiveData().getValue());
    }
}