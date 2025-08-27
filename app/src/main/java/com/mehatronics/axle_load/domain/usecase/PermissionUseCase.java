package com.mehatronics.axle_load.domain.usecase;

import android.app.Activity;
import android.content.Context;

public interface PermissionUseCase {
    void requestPermissions(Activity activity, int requestCode);

    boolean isBluetoothEnabled();

    boolean isGpsEnabled();

    Context getContext();
}