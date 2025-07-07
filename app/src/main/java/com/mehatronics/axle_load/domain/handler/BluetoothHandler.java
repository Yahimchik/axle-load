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
    private final DeviceViewModel deviceViewModel;
    private final BluetoothHandlerContract contract;
    private final ResourceProvider resourceProvider;
    private boolean userClosedDeviceDetails = false;
    private String deviceName;

    public BluetoothHandler(builder builder) {
        this.deviceViewModel = builder.deviceViewModel;
        this.contract = builder.contract;
        this.resourceProvider = builder.resourceProvider;
    }

    public void onClick(int axisNumber, AxisSide side) {
        deviceViewModel.onClick(axisNumber, side);
    }

    public void onReset(int axis) {
        var macsToReset = deviceViewModel.getMacsForAxis(axis);
        deviceViewModel.resetDevicesForAxis(axis);
        deviceViewModel.resetSelectedDevicesByMacs(macsToReset);
        deviceViewModel.markAsUnsaved();
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public void onConnect(int axis, AxisSide side) {
        String mac = deviceViewModel.getMacForAxisSide(axis, side);
        if (mac == null) {
            contract.showMessage("MAC-адрес не найден");
            return;
        }

        var scanned = deviceViewModel.getScannedDevices().getValue();
        if (scanned == null) {
            contract.showMessage("Список устройств пуст");
            return;
        }

        for (Device device : scanned) {
            if (device.getDevice().getAddress().equalsIgnoreCase(mac)) {
                onDeviceSelected(device);
                return;
            }
        }
        deviceViewModel.markAsSaved();
        contract.showMessage("Устройство не найдено по MAC: " + mac);
    }

    public void onDeviceDetailsFragmentClosed() {
        userClosedDeviceDetails = true;
        deviceViewModel.disconnect();
        deviceViewModel.clearDetails();
        contract.showMessage(resourceProvider.getString(R.string.disconnect_from, deviceName));
        deviceViewModel.markAsSaved();
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

    public static class builder {
        private DeviceViewModel deviceViewModel;
        private BluetoothHandlerContract contract;
        private ResourceProvider resourceProvider;

        public builder withModel(DeviceViewModel deviceViewModel) {
            this.deviceViewModel = deviceViewModel;
            return this;
        }

        public builder withContract(BluetoothHandlerContract contract) {
            this.contract = contract;
            return this;
        }

        public builder withResource(ResourceProvider provider) {
            this.resourceProvider = provider;
            return this;
        }

        public BluetoothHandler build() {
            return new BluetoothHandler(this);
        }
    }
}
