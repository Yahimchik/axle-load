package com.mehatronics.axle_load.notification;

import static android.R.id.content;
import static android.widget.Toast.LENGTH_SHORT;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;

public class NotificationManager {
    private final Context context;

    @Inject
    public NotificationManager(Context context) {
        this.context = context;
    }

    public void showConnectionFailureMessage() {
        Toast.makeText(context, "Failed to connect to device", LENGTH_SHORT).show();
    }

    public void showSnackBar(String message) {
        Snackbar.make(((Activity) context).findViewById(content), message, LENGTH_LONG).show();
    }
}
