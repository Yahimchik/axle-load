package com.mehatronics.axle_load.ble.handler;

import static android.R.id.content;
import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG;
import static java.lang.Boolean.TRUE;

import android.app.Activity;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.activity.BaseBluetoothActivity;
import com.mehatronics.axle_load.entities.Device;
import com.mehatronics.axle_load.entities.DeviceDetails;
import com.mehatronics.axle_load.fragment.ConfigureFragment;
import com.mehatronics.axle_load.viewModel.BluetoothViewModel;

import javax.inject.Inject;

public class BluetoothHandler {
    private final BluetoothViewModel bluetoothViewModel;
    private final BaseBluetoothActivity activity;

    @Inject
    public BluetoothHandler(BluetoothViewModel bluetoothViewModel, BaseBluetoothActivity activity) {
        this.bluetoothViewModel = bluetoothViewModel;
        this.activity = activity;
    }

    public void initConfigureButton(Button configureButton) {
        if (configureButton != null) {
            configureButton.setOnClickListener(v -> {
                if (!activity.isFinishing()) {
                    activity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.nav_host_fragment, new ConfigureFragment())
                            .addToBackStack(null)
                            .commit();
                }
            });
        }
    }

    public void handleDeviceDetails(DeviceDetails deviceDetails) {
        activity.loadingManager.showLoading(false);
        if (deviceDetails != null && TRUE.equals(bluetoothViewModel.isConnectedLiveData().getValue())) {
            if (activity.deviceNavigator.isFragmentNotVisible()) {
                activity.deviceNavigator.showFragment();
            }
        }
    }

    public void handleConnectionState(Boolean isConnected) {
        if (!isConnected && activity.isAttemptingToConnect) {
            Snackbar.make(((Activity) activity).findViewById(content), "Failed to connect to device", LENGTH_LONG).show();
            activity.loadingManager.showLoading(false);
            activity.isAttemptingToConnect = false;
        }

        if (isConnected) {
            activity.isAttemptingToConnect = false;
        }
    }

    public void onDeviceSelected(Device device) {
        activity.loadingManager.showLoading(true);
        activity.isAttemptingToConnect = true;
        bluetoothViewModel.connectToDevice(device);
    }
}
