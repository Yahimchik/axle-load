package com.mehatronics.axle_load.data.service.impl;

import android.app.Activity;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;

import com.mehatronics.axle_load.R;
import com.mehatronics.axle_load.data.service.PermissionHandlerService;

import javax.inject.Inject;

public class PermissionHandlerServiceImpl implements PermissionHandlerService {

    @Inject
    public PermissionHandlerServiceImpl() {
    }

    @Override
    public void getPermissions(Activity activity, Intent intent, int titleResId, int messageResId, int requestCode) {
        AlertDialog dialog = new AlertDialog
                .Builder(activity)
                .setTitle(activity.getString(titleResId))
                .setMessage(activity.getString(messageResId))
                .setPositiveButton(R.string.yes, (dialogInterface, which) -> activity.startActivityForResult(intent, requestCode))
                .setNegativeButton(R.string.no, null)
                .setCancelable(false)
                .create();
        dialog.show();
    }
}