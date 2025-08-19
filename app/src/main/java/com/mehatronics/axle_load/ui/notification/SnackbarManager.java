package com.mehatronics.axle_load.ui.notification;

import android.app.Activity;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SnackbarManager {
    @Inject
    public SnackbarManager() {
    }

    public void showMessage(View view, String message) {
        showMessage(view, message, null);
    }

    public void showMessage(View view, String message, Runnable onDismiss) {
        if (view != null && message != null) {
            Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
            if (onDismiss != null) {
                snackbar.addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        onDismiss.run();
                    }
                });
            }
            snackbar.show();
        }
    }

    public void showMessage(Activity activity, String message) {
        showMessage(activity, message, null);
    }

    public void showMessage(Activity activity, String message, Runnable onDismiss) {
        if (activity != null && message != null) {
            View rootView = activity.findViewById(android.R.id.content);
            Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG);
            if (onDismiss != null) {
                snackbar.addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        onDismiss.run();
                    }
                });
            }
            snackbar.show();
        }
    }
}