package com.mehatronics.axle_load.service;

import android.content.Context;

public interface PermissionService {
    String[] getRequiredPermissions();

    boolean isBluetoothEnabled();

    boolean isLocationEnabled();

    Context context();
}
