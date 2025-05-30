package com.mehatronics.axle_load.domain.usecase;

import android.app.Activity;
import android.content.Context;

import androidx.core.app.ActivityCompat;

import com.mehatronics.axle_load.service.PermissionService;

import javax.inject.Inject;

public class PermissionUseCase {
    private final PermissionService permissionService;

    @Inject
    public PermissionUseCase(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    public void requestPermissions(Activity activity, int requestCode) {
        ActivityCompat.requestPermissions(
                activity,
                permissionService.getRequiredPermissions(),
                requestCode
        );
    }

    public boolean isBluetoothEnabled() {
        return permissionService.isBluetoothEnabled();
    }

    public boolean isGpsEnabled() {
        return permissionService.isLocationEnabled();
    }

    public Context getContext() {
        return permissionService.context();
    }
}
