package com.mehatronics.axle_load.handler;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.StringRes;

public interface PermissionHandler {
    void getPermissions(Activity activity, Intent intent, @StringRes int titleResId, @StringRes int messageResId, int requestCode);
}
