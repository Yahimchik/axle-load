package com.mehatronics.axle_load.security.permissions.observer;

import static com.mehatronics.axle_load.security.permissions.helper.PermissionDialogHelper.showPermissionDialog;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;

import com.mehatronics.axle_load.security.permissions.usecase.PermissionUseCase;
import com.mehatronics.axle_load.viewModel.PermissionsViewModel;

import javax.inject.Inject;

public class PermissionObserver {
    private final Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    private final Intent gpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    private final PermissionsViewModel permissionsViewModel;
    private final PermissionUseCase permissionUseCase;
    private final Activity activity;

    @Inject
    public PermissionObserver(Activity activity, PermissionUseCase permissionUseCase,
                              PermissionsViewModel permissionsViewModel) {
        this.activity = activity;
        this.permissionUseCase = permissionUseCase;
        this.permissionsViewModel = permissionsViewModel;
    }

    public void observePermissionsStatus(LifecycleOwner owner) {
        Log.d("MyTag", "Запрос разрешений");
        permissionsViewModel.getPermissionsStatus().observe(owner, required ->
                permissionUseCase.requestPermissions(activity, 1));

        permissionsViewModel.isGpsRequired().observe(owner, required -> {
            if (required) {
                showPermissionDialog(activity, "Включение GPS",
                        "Для использования приложения требуется включить GPS.",
                        gpsIntent, 2);
            }
        });

        permissionsViewModel.isBluetoothRequired().observe(owner, required -> {
            if (required) {
                showPermissionDialog(activity, "Включение Bluetooth",
                        "Для использования приложения требуется включить Bluetooth.",
                        bluetoothIntent, 3);
            }
        });
    }
}
