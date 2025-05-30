package com.mehatronics.axle_load.handler;

import android.app.Activity;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;

import com.mehatronics.axle_load.R;

import javax.inject.Inject;

public class DefaultPermissionHandler implements PermissionHandler {

    @Inject
    public DefaultPermissionHandler() {
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
