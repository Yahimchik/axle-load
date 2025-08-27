package com.mehatronics.axle_load.data.service;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.StringRes;

public interface PermissionHandlerService {
    void getPermissions(Activity activity, Intent intent, @StringRes int titleResId, @StringRes int messageResId, int requestCode);
}