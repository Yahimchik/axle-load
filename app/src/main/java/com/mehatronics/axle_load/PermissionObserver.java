package com.mehatronics.axle_load;

import static com.mehatronics.axle_load.R.string.dialog_bluetooth_message;
import static com.mehatronics.axle_load.R.string.dialog_bluetooth_title;
import static com.mehatronics.axle_load.R.string.dialog_gps_message;
import static com.mehatronics.axle_load.R.string.dialog_gps_title;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.provider.Settings;

import androidx.lifecycle.LifecycleOwner;

import com.mehatronics.axle_load.domain.viewModel.PermissionsViewModel;
import com.mehatronics.axle_load.handler.PermissionHandler;

import javax.inject.Inject;

public class PermissionObserver {
    private final PermissionHandler permissionHandler;
    private PermissionsViewModel permissionsViewModel;
    private Activity activity;

    private final Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    private final Intent gpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);

    @Inject
    public PermissionObserver(PermissionHandler permissionHandler) {
        this.permissionHandler = permissionHandler;
    }

    public void setPermissionsViewModel(PermissionsViewModel permissionsViewModel) {
        this.permissionsViewModel = permissionsViewModel;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void startPermissionFlow() {
        permissionsViewModel.checkPermissions();
    }

    public void observePermissionsStatus(LifecycleOwner owner) {
        permissionsViewModel.getRequestPermissionsTrigger().observe(owner,
                unused -> permissionsViewModel.requestPermissions(activity, 1));

        permissionsViewModel.isGpsRequired().observe(owner, required -> {
            if (required) {
                permissionHandler.getPermissions(activity, gpsIntent, dialog_gps_title, dialog_gps_message, 2);
            }
        });

        permissionsViewModel.isBluetoothRequired().observe(owner, required -> {
            if (required) {
                permissionHandler.getPermissions(activity, bluetoothIntent, dialog_bluetooth_title, dialog_bluetooth_message, 3);
            }
        });
    }
}

