package com.mehatronics.axle_load.domain.usecase.impl;

import android.app.Activity;
import android.content.Context;

import androidx.core.app.ActivityCompat;

import com.mehatronics.axle_load.domain.usecase.PermissionUseCase;
import com.mehatronics.axle_load.data.service.PermissionService;

import javax.inject.Inject;

public class PermissionUseCaseImpl implements PermissionUseCase {
    private final PermissionService permissionService;

    @Inject
    public PermissionUseCaseImpl(PermissionService permissionService) {
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