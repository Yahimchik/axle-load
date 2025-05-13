package com.mehatronics.axle_load.security.permissions.service.impl;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.mehatronics.axle_load.security.permissions.service.PermissionService;

import javax.inject.Inject;

public class PermissionServiceImpl implements PermissionService {
    private final Context context;

    @Inject
    public PermissionServiceImpl(Context context) {
        this.context = context;
    }

    @Override
    public String[] getRequiredPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Log.d("MyTag", "Получение необходимых разрешений");
            return new String[]{Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };
        } else {
            return new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        }
    }

    @Override
    public boolean isBluetoothEnabled() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    @Override
    public boolean isLocationEnabled() {
        return Settings.Secure
                .getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE, 0)
                != Settings.Secure.LOCATION_MODE_OFF;
    }

    @Override
    public Context getContext() {
        return context;
    }
}

