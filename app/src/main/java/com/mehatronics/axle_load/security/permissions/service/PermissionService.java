package com.mehatronics.axle_load.security.permissions.service;

import android.content.Context;

public interface PermissionService {
    String[] getRequiredPermissions();

    boolean isBluetoothEnabled();

    boolean isLocationEnabled();

    Context getContext();
}
