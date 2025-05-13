package com.mehatronics.axle_load.security.permissions.helper;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

public class PermissionDialogHelper {
    public static void showPermissionDialog(Activity activity, String title, String message,
                                            Intent intent, int requestCode) {
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Да", (dialog, which) -> {
                    try {
                        activity.startActivityForResult(intent, requestCode);
                    } catch (SecurityException e) {
                        Toast.makeText(activity, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Нет", null)
                .setCancelable(false)
                .show();
    }
}
